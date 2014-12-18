package demo.nosql.comment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Test;

import demo.exception.InvalidInputException;
import demo.nosql.comment.CommentDbImpl;
import demo.nosql.comment.exception.CommentDBIsFullException;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentId;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.page.PageConverter;
import demo.nosql.comment.page.PageStorage;
import demo.util.StreamUtil;
import demo.util.StringUtil;
import test.BaseTest;
import static java.util.concurrent.TimeUnit.*;
import static demo.nosql.comment.model.Comment.*;
import static demo.nosql.comment.model.CommentTestCommon.*;
import static demo.nosql.comment.page.PageConverter.*;
import static demo.nosql.comment.page.PageStorage.*;
import static demo.util.StringUtil.*;
import static demo.util.Util.*;

public class CommentDbTest extends BaseTest {
	
	int defaultFilesCount = 3;
	
	Url url1 = new Url("url1");
	Url url2 = new Url("url2");
	Url url3 = new Url("url3");
	Url url4 = new Url("url4");
	Comment comment = new Comment(1, "test");
	
	
	
	@Test
	public void test_create_backup() throws Exception {
		
		CommentDbImpl db = new CommentDbImpl(TEST_DIR, new PageConverter(2, 2));
		db.addAsync(url1, comment, 0).get();
		db.addAsync(url2, comment, 0).get();
		db.addAsync(url3, comment, 0).get();
		db.addAsync(url4, comment, 0).get();
		
		String path = TEST_DIR+"/BAK-dir";
		db.createBackup(path);
		assertTrue(path, new File(path).exists());
		
		db.createBackup(null);
		
		//same dir
		try {
			db.createBackup(TEST_PATH);
			fail_exception_expected();
		}catch (InvalidInputException e) {
			//ok
		}
	}
	
	
	@Test
	public void test_create_urls_snaphot() throws Exception {
		
		CommentDbImpl db = new CommentDbImpl(TEST_DIR, new PageConverter(10, 2));
		db.addAsync(url1, comment, 0).get();
		db.addAsync(url2, comment, 0).get();
		String path = TEST_DIR+"/test.BAK";
		db.createUrlsStateSnapshot(path);
		assertTrue(path, new File(path).exists());
		
		db.createUrlsStateSnapshot(null);
		
		//same dir
		try {
			db.createUrlsStateSnapshot(TEST_PATH+"/././urls.db");
			fail_exception_expected();
		}catch (InvalidInputException e) {
			//ok
		}
		
	}
	
	
	
	@Test
	public void test_add_to_old_page_after_created_new() throws Exception {
		

		
		
		CommentDbImpl db = new CommentDbImpl(TEST_DIR, new PageConverter(10, 2));
		db.addAsync(url1, comment, 0).get();
		db.addAsync(url2, comment, 0).get();
		assertEquals(defaultFilesCount, TEST_DIR.list().length);
		db.addAsync(url3, comment, 0).get();
		assertEquals(defaultFilesCount+1, TEST_DIR.list().length);
		
		db.addAsync(url1, comment, 0).get();
	}
	
	
	
	@Test
	public void test_add_to_old_page_after_reload() throws Exception{

		
		//first db
		CommentDbImpl db = new CommentDbImpl(TEST_DIR, new PageConverter(10, 2));
		db.addAsync(url1, comment, 0).get();
		db.addAsync(url2, comment, 0).get();
		assertEquals(defaultFilesCount, TEST_DIR.list().length);
		db.addAsync(url3, comment, 0).get();
		assertEquals(defaultFilesCount+1, TEST_DIR.list().length);
		
		CommentDbImpl newDb = new CommentDbImpl(TEST_DIR, new PageConverter(10, 2));
		newDb.addAsync(url1, comment, 0).get();
		newDb.addAsync(url2, comment, 0).get();
		newDb.addAsync(url3, comment, 0).get();
	}
	
	
	
	@Test
	public void test_concurrent_read_write() throws Exception{
		
		final int totalWrite = 100;
		
		final Url[] urls = new Url[]{new Url("url1"),new Url("url2"),new Url("url3")};
		
		final CommentDbImpl db = new CommentDbImpl(TEST_DIR);
		
		final Random r = new Random();
		final Object monitor = new Object();
		ScheduledExecutorService readers = Executors.newScheduledThreadPool(8);
		ScheduledExecutorService writers = Executors.newScheduledThreadPool(2);
		
		
		class Result {
			boolean done = false;
			Throwable readError = null;
			int createdCount = 0;
			ArrayList<Future<Long>> addedFutureList = new ArrayList<>();
		}
		
		final Result result = new Result();
		
		for (int i = 0; i < 20; i++) {
			
			//writers
			writers.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					
					//System.out.println(System.currentTimeMillis()+"  WRITE: "+Thread.currentThread().getName());
					Url url = urls[r.nextInt(3)];
					Future<Long> future = db.addAsync(url, new Comment(StringUtil.createStr('я', Comment.CONTENT_MAX_SIZE)), 0);
					
					synchronized (monitor) {
						if(result.done) return;
						result.addedFutureList.add(future);
						result.createdCount++;
						if(result.createdCount == totalWrite){
							result.done = true;
							monitor.notifyAll();
						}
					}
					
				}
			}, 20, 20, MILLISECONDS);
			
			//readers
			readers.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					
					//System.out.println(System.currentTimeMillis()+"  READ: "+Thread.currentThread().getName());
					Url url = urls[r.nextInt(3)];
					
					try {
						InputStream is = db.getPageStream(url, 1);
						if(is != null) StreamUtil.streamToStr(is);
					}catch (Throwable e) {
						synchronized (monitor) {
							if(result.done) return;
							result.readError = e;
							monitor.notifyAll();
						}
					}
				
					
				}
			}, 20, 20, MILLISECONDS);
		}
		
		
		synchronized (monitor) {
			if(result.createdCount < totalWrite) monitor.wait();
			writers.shutdownNow();
			readers.shutdownNow();
		}
		
		//check read errors
		assertNull(result.readError);
		
		//check write errors
		assertEquals(totalWrite, result.addedFutureList.size());
		for (Future<Long> future : result.addedFutureList) {
			assertNotNull(future.get());
		}
		
		
	}
	
	
	
	@Test
	public void test_max_pages_count()throws Exception{
		
		int pagesInFile = 2;
		CommentDbImpl db = new CommentDbImpl(TEST_DIR, new PageConverter(DEFAULT_COMMENTS_IN_PAGE, pagesInFile));
		
		for (int i = 0; i < DEFAULT_MAX_FILES_COUNT; i++) {
			for (int j = 0; j < pagesInFile; j++) {
				assertFalse(db.isFull());
				db.addAsync(new Url("url"+i+"-"+j), new Comment(i+"-"+j), 0).get();
			}
		}
		
		try {
			assertTrue(db.isFull());
			db.addAsync(new Url("overUrl"), new Comment("over"), 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertEquals(CommentDBIsFullException.class, e.getCause().getClass());
		}
		
		for (int i = 0; i < DEFAULT_MAX_FILES_COUNT; i++) {
			File file = new File(TEST_DIR, PageStorage.getFileName(i));
			assertTrue("not exists "+file, file.exists());
		}
		
	}
	
	
	@Test
	public void test_create_max_page_with_min_content() throws Exception{
		
		Url url = new Url("http://ya.ru");
		
		int commentsInPage = DEFAULT_COMMENTS_IN_PAGE;
		String[] pageContents = new String[commentsInPage];
		for (int i = 0; i < commentsInPage; i++) {
			pageContents[i] = ""+i;
		}
		String newPageContent = "old page";

		
		CommentDbImpl db = new CommentDbImpl(TEST_DIR);
		for (int i = 0; i < commentsInPage; i++) {
			db.addAsync(url, new Comment(new Date(), Long.MAX_VALUE, pageContents[i]), 0).get();
		}
		
		checkStreamToCommentsContent(db.getPageStream(url, 0), pageContents);
		assertNull(db.getPageStream(url, 1));
		
		db.addAsync(url, new Comment(newPageContent), 0).get();
		
		ArrayList<String> finalList = list(pageContents);
		finalList.add(newPageContent);
		
		checkStreamToCommentsContent(db.getPageStream(url, 0), array(finalList, String.class));
		assertNull(db.getPageStream(url, 1));
	}
	
	
	@Test
	public void test_create_max_page_with_max_content() throws Exception{
		
		Url url = new Url("http://ya.ru");
		
		int commentsInPage = DEFAULT_COMMENTS_IN_PAGE;
		String[] pageContents = new String[commentsInPage];
		for (int i = 0; i < commentsInPage; i++) {
			pageContents[i] = createStr('я', CONTENT_MAX_SIZE);
		}
		String newPageContent = "new page";

		
		CommentDbImpl db = new CommentDbImpl(TEST_DIR);
		for (int i = 0; i < commentsInPage; i++) {
			db.addAsync(url, new Comment(new Date(), Long.MAX_VALUE, pageContents[i]), 0).get();
		}
		
		checkStreamToCommentsContent(db.getPageStream(url, 0), pageContents);
		assertNull(db.getPageStream(url, 1));
		
		db.addAsync(url, new Comment(newPageContent), 0).get();
		checkStreamToCommentsContent(db.getPageStream(url, 0), pageContents);
		checkStreamToCommentsContent(db.getPageStream(url, 1), newPageContent);
	}
	
	
	@Test
	public void test_CRUD() throws Exception{
		
		Url url1 = new Url("http://ya.ru");
		Url url2 = new Url("google.com");
		String content1 = "123";
		String content2 = createStr('я', CONTENT_MAX_SIZE);
		String content3 = createStr('z', CONTENT_MAX_SIZE);
		String content4 = "абв\n\t</>\"";
		
		//create
		CommentDbImpl db = new CommentDbImpl(TEST_DIR);
		assertNotNull(db.addAsync(url1, new Comment(content1), 0).get());
		
		db.addAsync(url2, new Comment(content2), 0).get();
		
		
		//read
		checkStreamToCommentsContent(db.getPageStream(url1, 0), content1);
		checkStreamToCommentsContent(db.getPageStream(url2, 0), content2);
		
		//by index
		assertEquals(content1, db.getComment(new CommentId(url1, 0, 0)).getContent());
		assertNull(db.getComment(new CommentId(url1, 0, 1)));
		assertEquals(content2, db.getComment(new CommentId(url2, 0, 0)).getContent());
		
		
		//update
		db.addAsync(url1, new Comment(content3), 0).get();
		db.addAsync(url2, new Comment(content4), 0).get();
		checkStreamToCommentsContent(db.getPageStream(url1, 0), content1, content3);
		checkStreamToCommentsContent(db.getPageStream(url2, 0), content2, content4);
		
		
		//read again
		CommentDbImpl db2 = new CommentDbImpl(TEST_DIR);
		checkStreamToCommentsContent(db2.getPageStream(url1, 0), content1, content3);
		checkStreamToCommentsContent(db2.getPageStream(url2, 0), content2, content4);
	}
	
}
