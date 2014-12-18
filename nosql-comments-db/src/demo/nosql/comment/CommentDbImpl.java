package demo.nosql.comment;

import static demo.nosql.comment.CommentDbCommon.*;
import static demo.nosql.comment.log.LogEvent.*;
import static demo.util.ExceptionUtil.*;
import static demo.util.ZipUtil.*;
import static demo.util.concurrent.ExecutorsUtil.*;
import static java.lang.System.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import demo.exception.io.BrokenFileException;
import demo.nosql.comment.exception.CommentDBIsFullException;
import demo.nosql.comment.log.SessionLog;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentId;
import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.model.FileInfo;
import demo.nosql.comment.model.PageId;
import demo.nosql.comment.model.PagesBlock;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.model.UrlLine;
import demo.nosql.comment.model.UrlState;
import demo.nosql.comment.page.PageConverter;
import demo.nosql.comment.page.PageStorage;
import demo.nosql.comment.url.UrlLineReader;
import demo.nosql.comment.url.UrlLineStorage;
import demo.util.ExceptionUtil;
import demo.util.FileUtil;
import demo.util.concurrent.ForSingleThread;
import demo.util.io.LimitInputStream;

public class CommentDbImpl implements CommentDb {

	private static final Log log = LogFactory.getLog(CommentDbImpl.class);

	
	private ExecutorService singleWriteService = newSingleThreadExecutor("CommentDb-write");
	//private ExecutorService singlePostWriteService = newSingleThreadExecutor("CommentDb-post-write");
	
	private PageStorage pageStorage;
	private UrlLineStorage urlLineStorage;
	//private JournalStorage journalStorage;
	private SessionLog sessionLog;
	
	
	
	//cache
	private ConcurrentHashMap<String, UrlState> urlStates = new ConcurrentHashMap<>();
	
	
	
	//params
	public final File root;
	public final PageConverter pageConverter;
	
	public CommentDbImpl(Properties props) throws IOException{
		this(props.getProperty("comments.path"));
	}
	
	public CommentDbImpl(String rootPath) throws IOException{
		this(new File(rootPath), new PageConverter());
	}
	
	public CommentDbImpl(File root) throws IOException{
		this(root, new PageConverter());
	}
	
	public CommentDbImpl(File root, PageConverter converter) throws IOException{
		this.root = root;
		this.pageConverter = converter;
		initFromFiles();
	}
	
	private void initFromFiles() throws IOException {
		
		log.info("init db...");
			
		root.mkdirs();
		sessionLog = new SessionLog(root);
		pageStorage = new PageStorage(root, pageConverter);
		urlLineStorage = new UrlLineStorage(root, sessionLog);
		//journalStorage = new JournalStorage(root, pageConverter);
		
		initCache();
		log.info("done.");
		
	}
	
	private void initCache() throws IOException {
		
		int readingLineIndex = 0;
		
		try {
		
			UrlLineReader reader = urlLineStorage.readLines();
			
			UrlLine line = reader.next();
			while(line != null){
				
				
				UrlState state = urlStates.get(line.url);
				if(state == null) {
					state = new UrlState();
					urlStates.put(line.url, state);
				}
				state.addBlock(line.toBlock());
				
				readingLineIndex++;
				line = reader.next();
			}
		}catch (BrokenFileException e) {
			
			String msg = "can't read url line "+readingLineIndex+": "+e.getMessage();
			msg += "\n" + "stopped init cache. inited: "+urlStates.size()+" urls";
			log.error(msg);
			sessionLog.error(READ_URLS_FILE_ERROR, msg);
			
		}catch (Throwable t) {
			sessionLog.error(READ_URLS_FILE_ERROR, "Unexpected exception while reading line "+readingLineIndex, t);
			if(t instanceof IOException) throw (IOException)t;
			else throw ExceptionUtil.getRuntimeExceptionOrThrowError(t);
		}
	}
	
	public static class AddResult {
		public Long createdTime;
		public AddResult(Long createdTime) {
			this.createdTime = createdTime;
		}
	}

	@Override
	public Future<Long> addAsync(final Url url, final Comment c, final int pageBlockIndexForNew) {
		
		Future<Long> future = singleWriteService.submit(new Callable<Long>() {

			@Override
			public Long call() throws Exception {
				return tryAdd(url, c, pageBlockIndexForNew).createdTime;
			}
		});
		return future;
	}
	
	
	public Future<AddResult> addAsyncFull(final Url url, final Comment c, final int pageBlockIndexForNew) {
		
		Future<AddResult> future = singleWriteService.submit(new Callable<AddResult>() {

			@Override
			public AddResult call() throws Exception {
				return tryAdd(url, c, pageBlockIndexForNew);
			}
		});
		return future;
	}
	
	
	
	private AddResult tryAdd(Url url, Comment c, int pageBlockIndexForNew) throws Exception {
		try {
			return add(url, c, pageBlockIndexForNew);
		}catch (Throwable t) {
			//because api returns the Future object - error can be never logged outside
			String msg = "can't add "+c+" to "+url;
			log.error(msg, t);
			sessionLog.error(ADD_COMMENT_ERROR, msg, t);
			throw ExceptionUtil.getExceptionOrThrowError(t);
		}
	}

	@ForSingleThread
	private AddResult add(Url url, Comment c, int pageBlockIndexForNew) throws Exception {
		
		if(pageStorage.isFull()) throw new CommentDBIsFullException();
		
		UrlState state = getState(url.val);
		if(state == null){
			createFirstBlock(url.val, c, pageBlockIndexForNew);
		} else {
			appendToBlock(url.val, c, state);
		}
		
		return new AddResult(c.created.getTime());
	}

	@ForSingleThread
	private void createFirstBlock(String url, Comment c, int pageBlockIndex) throws Exception {
		
		CommentsPage p = pageStorage.createPage(c);
		long lineOffset = urlLineStorage.createLine(pageBlockIndex, url, p);
		
		PagesBlock block = new PagesBlock(pageBlockIndex, lineOffset, p);
		putState(url, new UrlState(block));
	}
	
	@ForSingleThread
	private void appendToBlock(String url, Comment c, UrlState state)throws Exception {
		
		int blockSize = state.blocksSize();
		if(blockSize == 0) throw new IllegalStateException("can't append to empty blocks");
		
		PagesBlock curBlock = state.getBlock(blockSize-1);
		
		int oldPageIndex = curBlock.pagesSize()-1;
		int newPageIndex = oldPageIndex+1;
		CommentsPage cachePage = curBlock.getPage(oldPageIndex);
		
		CommentsPage resultPage = pageStorage.updateOrCreatePage(cachePage, c);
		
		//comment in cur page - update cur index in line:
		//[c1,c2,c3],[c4, ->c5<- ,null]
		if(resultPage.virtualOffset == cachePage.virtualOffset){
			urlLineStorage.updateLine(curBlock.offset, oldPageIndex, resultPage, cachePage);
			cachePage.limit = resultPage.limit;
			return;
		}
		
		//comment in next page - create new index in line:
		//[c1,c2,c3],[c4,c5,c6],->[c7,null,null]<-
		if( ! urlLineStorage.isFullLine(newPageIndex)){
			urlLineStorage.updateLine(curBlock.offset, newPageIndex, resultPage);
			curBlock.addPage(resultPage);
			return;
		}
		
		//comment in next page AND LINE IS FULL - create new line:
		//[c1,c2,c3],[c4,c5,c6],[c7,c8,c9]
		//->[c1,null,null]<-
		int newBlockIndex = curBlock.pageBlockIndex+1;
		long lineOffset = urlLineStorage.createLine(newBlockIndex, url, resultPage);
		PagesBlock newPageBlock = new PagesBlock(newBlockIndex, lineOffset, resultPage);
		state.addBlock(newPageBlock);
		
	}
	
	@Override
	public LimitInputStream getPageStream(PageId pageId) throws IOException {
		return getPageStream(pageId.url, pageId.pageIndex);
	}
	

	@Override
	public LimitInputStream getPageStream(Url url, int pageIndex) throws IOException{
		
		UrlState state = getState(url.val);
		if(state == null) return null;
		
		CommentsPage page = state.findPage(pageIndex);
		if(page == null) return null;
		
		return pageStorage.getPageStream(page);
	}
	
	
	@Override
	public boolean isFull() {
		return pageStorage.isFull();
	}
	
	@Override
	public void close() throws IOException {
		singleWriteService.shutdownNow();
		pageStorage.close();
		urlLineStorage.close();
	}
	
	
	@Override
	public int getUrlsCount() {
		return urlStates.size();
	}
	
	@Override
	public int getCommentsPagesCount(Url url) {
		return getPagesCount(url.val);
	}
	
	@Override
	public int getAllCommentsPagesCount() {
		int count = 0;
		for (String url : urlStates.keySet()) {
			count += getPagesCount(url);
		}
		return count;
	}
	
	@Override
	public Enumeration<String> getUrlsEnumeration(){
		return urlStates.keys();
	}
	
	
	UrlState getState(String url){
		return urlStates.get(url);
	}
	
	private void putState(String url, UrlState state) {
		urlStates.put(url, state);
	}

	private int getPagesCount(String url) {
		UrlState state = urlStates.get(url);
		if(state == null) return 0;
		int count = 0;
		for (int i = 0; i < state.blocksSize(); i++) {
			PagesBlock block = state.getBlock(i);
			count += block.pagesSize();
		}
		return count;
	}

	@Override
	public void createUrlsStateSnapshot(final String resultFilePath) throws IOException {
		Future<Void> future = singleWriteService.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				urlLineStorage.createSnapshot(resultFilePath);
				return null;
			}
		});
		try {
			future.get();
		}catch (Exception e) {
			throw unwrapIOException(e);
		}
	}

	@Override
	public void createBackup(String backupDirPath) throws IOException {
		
		try {
			
			final File dir = backupDirPath != null? new File(backupDirPath) : new File(root, "backup-"+currentTimeMillis());
			dir.mkdirs();
			
			String msg = "Create data backup to "+dir.getPath();
			sessionLog.info(msg);
			log.info(msg);
			
			//copy all files
			final File urlsCopy = new File(dir, UrlLineStorage.FILE_NAME);
			Future<List<FileInfo>> future = singleWriteService.submit(new Callable<List<FileInfo>>() {
				@Override
				public List<FileInfo> call() throws Exception {
					urlLineStorage.createSnapshot(urlsCopy.getPath());
					return pageStorage.getCurFiles();
				}
			});
			List<FileInfo> pages = future.get();
			final ArrayList<File> pagesCopies = new ArrayList<>();
			for (final FileInfo pageFile : pages) {
				singleWriteService.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						File copy = new File(dir, pageFile.f.getName());
						FileUtil.copyFile(pageFile.f, copy);
						pagesCopies.add(copy);
						return null;
					}
				}).get();
			}
			
			//create zips
			File urlsZipFile = new File(dir, UrlLineStorage.FILE_NAME+".zip");
			zipSingleFile(urlsCopy, urlsZipFile);
			urlsCopy.delete();
			for (File file : pagesCopies) {
				File zipFile = new File(dir, file.getName()+".zip");
				zipSingleFile(file, zipFile);
				file.delete();
			}
		}catch (Exception e) {
			throw unwrapIOException(e);
		}
		
		
	}

	@Override
	public Comment getComment(CommentId commentId) throws IOException {
		return getCommentFromStream(getPageStream(commentId.getPageId()), commentId);
		
	}

}
