package demo.nosql.comment.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;

import demo.nosql.comment.exception.PageStorageIsFullException;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.model.FileInfo;
import demo.nosql.comment.page.PageConverter.NewPageToBytesResult;
import demo.nosql.comment.page.PageConverter.NextCommentToBytesResult;
import demo.util.NumberUtil;
import demo.util.concurrent.ForSingleThread;
import demo.util.file.FileSortAsc;
import demo.util.io.LimitInputStream;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static demo.nosql.comment.debug.DebugTool.*;
import static demo.nosql.comment.model.FileInfo.*;
import static demo.util.FileUtil.*;
import static demo.util.MathUtil.*;
import static demo.util.Util.*;

public class PageStorage {
	
	@SuppressWarnings("unused")
	private static final Log log = getLog(PageStorage.class);
	
	
	public static final String FILE_NAME_PATTERN = "pages-";
	public static final String FILE_EXT = ".db";
	public static final int DEFAULT_MAX_FILES_COUNT = 50;
	
	public final int maxFilesCount;
	public final PageConverter c;
	
	private File root;
	private CopyOnWriteArrayList<FileInfo> files = new CopyOnWriteArrayList<>();
	
	
	public PageStorage(File root) throws IOException {
		this(root, new PageConverter(), getMaxFilesCount());
	}
	
	public PageStorage(File root, PageConverter converter) throws IOException {
		this(root, converter, getMaxFilesCount());
	}
	
	private static int getMaxFilesCount(){
		return tryParseInt(System.getProperty("sc.comments_files"), DEFAULT_MAX_FILES_COUNT);
	}
	
	public PageStorage(File root, PageConverter converter, int maxFilesCount) throws IOException {
		
		this.maxFilesCount = maxFilesCount;
		this.c = converter;
		
		this.root = root;
		root.mkdirs();
		
		File[] list = listFilesWithNameStart(root, FILE_NAME_PATTERN);
		if( ! isEmpty(list)){
			openFiles(list);
		}
		if(isEmpty(files)){
			createFile(0);
		}
	}

	/**
	 * Create new page in current 'page-...db' file
	 */
	@ForSingleThread
	public CommentsPage createPage(Comment comment) throws IOException {
		
		comment.created = new Date();
		
		NewPageToBytesResult bytesData = c.newPageToBytes(comment);
		FileInfo file = getLastOrCreateNewFile();
		
		debug_BeforeWriteToPageFile(file);
		
		//page always has same size
		long fileEndOffset = roundDown(file.length, c.pageBytesCount);
		file.raf.seek(fileEndOffset);
		file.raf.write(bytesData.pageBytes);
		file.length = file.raf.getFilePointer();
		
		debug_AfterWriteToPageFile(file);
		
		long virtualOffset = (long)getLastFileIndex()*c.fileBytesCount+fileEndOffset;
		
		return new CommentsPage(virtualOffset, bytesData.commentBytes.length);
		
	}
	
	@ForSingleThread
	public CommentsPage updateOrCreatePage(CommentsPage commentsPage, Comment comment) throws IOException {
		return updateOrCreatePage(commentsPage.virtualOffset, commentsPage.limit, comment);
	}
	
	@ForSingleThread
	public CommentsPage updateOrCreatePage(long virtualOffset, int limit, Comment comment) throws IOException {
		
		boolean canAppendToPage = limit + c.commentBytesCount < c.pageBytesCount - 1;
		
		//page is full need create new page
		if(!canAppendToPage){
			return createPage(comment);
		}
		
		int fileIndex = getFileIndex(virtualOffset);
		FileInfo file = files.get(fileIndex);
		checkState(file != null, "info is null by fileIndex: "+fileIndex);
		checkState(file.raf != null, "info.raf is null for "+file);
		
		
		long pageStartOffset = getPageStartOffset(virtualOffset);
		long lastCommentOffset = pageStartOffset + limit;
		
		comment.created = new Date();
		NextCommentToBytesResult bytesData  = c.nextCommentToBytes(comment);
		
		debug_BeforeUpdatePageFile(file);
		
		//append to cur page
		file.raf.seek(lastCommentOffset);
		file.raf.write(bytesData.nextCommentBytes);
		
		debug_AfterUpdatePageFile(file);
		
		return new CommentsPage(virtualOffset, limit + bytesData.nextCommentBytes.length);
	}





	public LimitInputStream getPageStream(CommentsPage page) throws IOException {
		return getPageStream(page.virtualOffset, page.limit);
	}
	
	
	public LimitInputStream getPageStream(long virtualOffset, int limit) throws IOException{
		
		if(virtualOffset < 0) 
			throw new IllegalArgumentException("virtualOffset < 0");
		
		int fileIndex = getFileIndex(virtualOffset);
		FileInfo file = files.get(fileIndex);
		
		long pageStartOffset = getPageStartOffset(virtualOffset);

		FileInputStream fis = new FileInputStream(file.f);
		fis.skip(pageStartOffset);
		return new LimitInputStream(fis, limit);
	}
	
	public boolean isFull(){
		int lastIndex = getLastFileIndex();
		FileInfo file = files.get(lastIndex);
		if(c.isFull(file)) lastIndex++;
		return ! isValidFileIndex(lastIndex);
	}
	
	
	public void close() throws IOException{
		closeRAFs(files);
	}


	private FileInfo getLastOrCreateNewFile() throws IOException {
		int lastIndex = getLastFileIndex();
		FileInfo file = files.get(lastIndex);
		if(c.isFull(file)){
			lastIndex++;
			file = createFile(lastIndex);
		}
		return file;
	}


	private int getLastFileIndex() {
		return files.size()-1;
	}
	
	private int getFileIndex(long virtualOffset) {
		int fileIndex = (int)(virtualOffset / c.fileBytesCount);
		if(fileIndex > getLastFileIndex()) throw new IllegalArgumentException("invalid offset "+virtualOffset+" for pages size "+(getLastFileIndex()+1));
		return fileIndex;
	}
	
	private long getPageStartOffset(long virtualOffset) {
		return virtualOffset % c.fileBytesCount;
	}
	
	
	private void openFiles(File[] list) throws IOException {
		sort(list, new FileSortAsc());
		for (int i = 0; i < list.length; i++) {
			File f = list[i];
			if( ! f.isFile()) continue;
			if( ! f.getName().startsWith(FILE_NAME_PATTERN)) continue;
			files.add(new FileInfo(f));
		}
	}
	
	private FileInfo createFile(int index) throws IOException {
		
		if( ! isValidFileIndex(index)) throw new PageStorageIsFullException("max file count: "+maxFilesCount);
			
		File f = new File(root, getFileName(index));
		if(f.exists()) throw new IllegalStateException("file "+f+" already exists");
		f.createNewFile();
		
		FileInfo file = new FileInfo(f);
		files.add(file);
		return file;
	}
	
	private boolean isValidFileIndex(int index) {
		return index < maxFilesCount;
	}


	public static String getFileName(int index) {
		return FILE_NAME_PATTERN + NumberUtil.zeroFormattedStr(index, 4) + FILE_EXT;
	}

	@ForSingleThread
	public List<FileInfo> getCurFiles(){
		return unmodifiableList(files);
	}
	

}
