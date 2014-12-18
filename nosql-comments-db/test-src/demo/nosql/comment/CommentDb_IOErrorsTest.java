package demo.nosql.comment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import demo.nosql.comment.CommentDbImpl;
import demo.nosql.comment.debug.DebugTool;
import demo.nosql.comment.debug.DebugTool.DebugListener;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.FileInfo;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.page.PageConverter;
import demo.util.FileUtil;
import demo.util.StreamUtil;
import demo.util.concurrent.ExecutorsUtil;
import demo.util.json.GsonUtil;
import test.BaseTest;
import test.TestException;
import static demo.util.StreamUtil.*;
import static demo.util.StringUtil.*;

public class CommentDb_IOErrorsTest extends BaseTest {
	
	static Gson gson = GsonUtil.defaultGson;
	
	Url url1 = new Url("url111111111111111111111111111");
	Url url2 = new Url("url222222222222222222222222222");
	Url url3 = new Url("url333333333333333333333333333");
	Comment comment1 = new Comment(1, "test1");
	Comment comment2 = new Comment(1, "test2");
	Comment comment3 = new Comment(1, "test3");
	Comment comment4 = new Comment(1, "test4");
	Comment comment5 = new Comment(1, "test5");
	Comment comment6 = new Comment(1, "test6");
	Comment comment7 = new Comment(1, "test7");
	Comment comment8 = new Comment(1, "test8");
	Comment comment9 = new Comment(1, "test9");
	Comment comment10 = new Comment(1, "test10");
	
	@Before
	public void before(){
		DebugTool.ENABLED = true;
		DebugTool.clear();
	}
	
	@After
	public void after(){
		DebugTool.ENABLED = false;
		DebugTool.clear();
	}
	
	
	
	
	
	@Test
	public void test_09_stop_with_not_full_update_line() throws Exception {
		
		//new db - read
		{
			File dir1 = new File(TEST_DIR, "1");
			situation09(dir1);
			CommentDbImpl otherDb = new CommentDbImpl(dir1, new PageConverter(5, 2));
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(4, list.size());
		}
		
		//new db - write
		{
			File dir2 = new File(TEST_DIR, "2");
			situation09(dir2);
			
			CommentDbImpl otherDb = new CommentDbImpl(dir2, new PageConverter(5, 2));
			otherDb.addAsync(url1, comment5, 0).get();
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(5, list.size());
			assertEquals(comment1.getContent(), list.get(0).getContent());
			assertEquals(comment2.getContent(), list.get(1).getContent());
			assertEquals(comment3.getContent(), list.get(2).getContent());
			assertEquals(comment4.getContent(), list.get(3).getContent());
			assertEquals(comment5.getContent(), list.get(4).getContent());
		}
		
	}
	
	private void situation09(final File dir) throws Exception {
		
		ExecutorService single = ExecutorsUtil.newSingleThreadExecutor("test");
		Future<Void> future = single.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(5, 2));
				db.addAsync(url2, comment2, 0).get();
				
				db.addAsync(url1, comment1, 0).get();
				db.addAsync(url1, comment2, 0).get();
				db.addAsync(url1, comment3, 0).get();
				db.addAsync(url1, comment4, 0).get();
				DebugTool.add(new DebugListener() {
					@SuppressWarnings({ "unchecked", "deprecation" })
					@Override
					public void onEvent(String name, Object data) throws Exception {
						if(name.equals("debug_AfterUpdateUrlLine")) {
							Map<String, Object> map = (Map<String, Object>)data;
							FileInfo file = (FileInfo)map.get("file");
							Long offset = (Long)map.get("offset");
							byte[] bytes = (byte[])map.get("bytes");
							int length = bytes.length;
							bytes[length-1] = 0;
							
							file.raf.seek(offset);
							file.raf.write(bytes);
							
							DebugTool.THREAD_KILLED_SIMULATION = true;
							Thread.currentThread().stop();
						}
					}
				});
				
				
				try {
					db.addAsync(url1, comment5, 0).get();
					fail_exception_expected();
				}catch (Exception e) {
					//ok
				}
				
				return null;
			}
		});
		
		
		future.get();
		DebugTool.clear();
	}
	
	
	
	@Test
	public void test_08_error_not_full_update_line_with_invalid_limit() throws Exception {

		{
			File dir1 = new File(TEST_DIR, "1");
			CommentDbImpl db = situation08(dir1);
			List<Comment> list = fromJson(db.getPageStream(url1, 0));
			assertEquals(4, list.size());
			db.addAsync(url1, comment5, 0).get();
			list = fromJson(db.getPageStream(url1, 0));
			assertEquals(5, list.size());
		}
		
		//new db - read
		{
			File dir2 = new File(TEST_DIR, "2");
			situation08(dir2);
			CommentDbImpl otherDb = new CommentDbImpl(dir2, new PageConverter(5, 2));
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(4, list.size());
		}
		
		//new db - write
		{
			File dir3 = new File(TEST_DIR, "3");
			situation08(dir3);
			
			CommentDbImpl otherDb = new CommentDbImpl(dir3, new PageConverter(5, 2));
			otherDb.addAsync(url1, comment5, 0).get();
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(5, list.size());
			assertEquals(comment1.getContent(), list.get(0).getContent());
			assertEquals(comment2.getContent(), list.get(1).getContent());
			assertEquals(comment3.getContent(), list.get(2).getContent());
			assertEquals(comment4.getContent(), list.get(3).getContent());
			assertEquals(comment5.getContent(), list.get(4).getContent());
		}
		
	}
	
	private CommentDbImpl situation08(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(5, 2));
		db.addAsync(url2, comment2, 0).get();
		
		db.addAsync(url1, comment1, 0).get();
		db.addAsync(url1, comment2, 0).get();
		db.addAsync(url1, comment3, 0).get();
		db.addAsync(url1, comment4, 0).get();
		DebugTool.add(new DebugListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onEvent(String name, Object data) throws Exception {
				if(name.equals("debug_AfterUpdateUrlLine")) {
					Map<String, Object> map = (Map<String, Object>)data;
					FileInfo file = (FileInfo)map.get("file");
					Long offset = (Long)map.get("offset");
					byte[] bytes = (byte[])map.get("bytes");
					int length = bytes.length;
					bytes[length-1] = 0;
					
					file.raf.seek(offset);
					file.raf.write(bytes);
					
					
					throw new TestException();
				}
			}
		});
		try {
			db.addAsync(url1, comment5, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	
	
	
	@Test
	public void test_07_error_before_create_next_page() throws Exception {
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation07(dir1);
		afterCreateError03(db, dir1);

		File dir2 = new File(TEST_DIR, "2");
		situation07(dir2);
		afterCreateError04(dir2);
	}
	
	private CommentDbImpl situation07(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		db.addAsync(url1, comment1, 0).get();
		db.addAsync(url2, comment2, 0).get();
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data) throws Exception {
				if(name.equals("debug_BeforeWriteToPageFile")) throw new TestException();
			}
		});
		try {
			db.addAsync(url3, comment3, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	private void afterCreateError03(CommentDbImpl db, File dir) throws Exception{
		
		assertEquals(comment1.getContent(), fromJson(db.getPageStream(url1, 0)).get(0).getContent());
		assertEquals(comment2.getContent(), fromJson(db.getPageStream(url2, 0)).get(0).getContent());
		assertNull(db.getPageStream(url3, 0));
		
		db.addAsync(url1, comment4, 0).get();
		db.addAsync(url2, comment5, 0).get();
		db.addAsync(url3, comment6, 0).get();
		
		assertEquals(comment4.getContent(), fromJson(db.getPageStream(url1, 0)).get(1).getContent());
		assertEquals(comment5.getContent(), fromJson(db.getPageStream(url2, 0)).get(1).getContent());
		assertEquals(comment6.getContent(), fromJson(db.getPageStream(url3, 0)).get(0).getContent());
		
		//new db
		CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
		otherDb.addAsync(url3, comment7, 0).get();
		List<Comment> list = fromJson(otherDb.getPageStream(url3, 0));
		assertEquals(comment6.getContent(), list.get(0).getContent());
		assertEquals(comment7.getContent(), list.get(1).getContent());
	}
	
	private void afterCreateError04(File dir) throws Exception {
		//new db
		{
			CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
			
			assertEquals(comment1.getContent(), fromJson(otherDb.getPageStream(url1, 0)).get(0).getContent());
			assertNull(otherDb.getPageStream(url3, 0));
			
			otherDb.addAsync(url1, comment3, 0).get();
			otherDb.addAsync(url2, comment4, 0).get();
			otherDb.addAsync(url3, comment5, 0).get();
			List<Comment> list = fromJson(otherDb.getPageStream(url3, 0));
			assertEquals(comment5.getContent(), list.get(0).getContent());
		}
		//new db again
		{
			CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
			List<Comment> list = fromJson(otherDb.getPageStream(url3, 0));
			assertEquals(1, list.size());
			assertEquals(comment5.getContent(), list.get(0).getContent());
		}
	}
	
	
	
	
	
	
	
	@Test
	public void test_06_error_not_full_comment_write_in_update() throws Exception {
		
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation06(dir1);
		afterUpdateError01(db, dir1);

		File dir2 = new File(TEST_DIR, "2");
		situation06(dir2);
		afterUpdateError02(dir2);
		
	}
	
	
	private CommentDbImpl situation06(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		db.addAsync(url1, comment1, 0).get();
		
		final byte[] firstWritted = getBytesUTF8(streamToStr(db.getPageStream(url1, 0)));
		
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data)throws Exception {
				if(name.equals("debug_AfterUpdatePageFile")){
					FileInfo file = (FileInfo) data;
					byte[] writted = getBytesUTF8(streamToStr(new FileInputStream(file.f)));
					for (int i = (firstWritted.length + firstWritted.length/2); i < writted.length; i++) {
						writted[i] = 0;
					}
					file.raf.close();
					FileUtil.writeFileUTF8(file.f, getStrUTF8(writted), false);
					file.raf = new RandomAccessFile(file.f, "rw");
					throw new TestException();
				}
			}
		});
		try {
			db.addAsync(url1, comment2, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	
	@Test
	public void test_05_error_before_update_pages_file() throws Exception {
		
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation05(dir1);
		afterUpdateError01(db, dir1);

		File dir2 = new File(TEST_DIR, "2");
		situation05(dir2);
		afterUpdateError02(dir2);
		
	}
	
	private CommentDbImpl situation05(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		db.addAsync(url1, comment1, 0).get();
		
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data) throws Exception {
				if(name.equals("debug_BeforeUpdatePageFile")) throw new TestException();
			}
		});
		try {
			db.addAsync(url1, comment2, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	private void afterUpdateError01(CommentDbImpl db, File dir) throws Exception{
		
		assertEquals(comment1.getContent(), fromJson(db.getPageStream(url1, 0)).get(0).getContent());
		
		db.addAsync(url1, comment2, 0).get();
		db.addAsync(url2, comment3, 0).get();
		
		assertEquals(comment1.getContent(), fromJson(db.getPageStream(url1, 0)).get(0).getContent());
		assertEquals(comment2.getContent(), fromJson(db.getPageStream(url1, 0)).get(1).getContent());
		assertEquals(comment3.getContent(), fromJson(db.getPageStream(url2, 0)).get(0).getContent());
		
		//new db
		CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
		otherDb.addAsync(url1, comment4, 0).get();
		List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
		assertEquals(comment1.getContent(), list.get(0).getContent());
		assertEquals(comment2.getContent(), list.get(1).getContent());
		assertEquals(comment4.getContent(), list.get(2).getContent());
		assertEquals(comment3.getContent(), fromJson(otherDb.getPageStream(url2, 0)).get(0).getContent());
	}
	
	private void afterUpdateError02(File dir) throws Exception {
		//new db
		{
			CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
			
			assertEquals(comment1.getContent(), fromJson(otherDb.getPageStream(url1, 0)).get(0).getContent());
			
			otherDb.addAsync(url1, comment2, 0).get();
			otherDb.addAsync(url2, comment3, 0).get();
			otherDb.addAsync(url1, comment4, 0).get();
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(comment1.getContent(), list.get(0).getContent());
			assertEquals(comment2.getContent(), list.get(1).getContent());
			assertEquals(comment4.getContent(), list.get(2).getContent());
			assertEquals(comment3.getContent(), fromJson(otherDb.getPageStream(url2, 0)).get(0).getContent());
		}
		//new db again
		{
			CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(3, list.size());
			assertEquals(comment1.getContent(), list.get(0).getContent());
			assertEquals(comment2.getContent(), list.get(1).getContent());
			assertEquals(comment4.getContent(), list.get(2).getContent());
			assertEquals(comment3.getContent(), fromJson(otherDb.getPageStream(url2, 0)).get(0).getContent());
		}
	}
	
	
	
	
	
	
	
	@Test
	public void test_04_error_created_line_with_not_full_size() throws Exception {
		
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation04(dir1);
		afterCreateError01(db, dir1);

		File dir2 = new File(TEST_DIR, "2");
		situation04(dir2);
		afterCreateError02(dir2);
		
	}
	
	private CommentDbImpl situation04(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data)throws Exception {
				long firstLength = 0;
				if(name.equals("debug_BeforeWriteNewUrlLine")){
					FileInfo file = (FileInfo) data;
					firstLength = file.raf.getFilePointer();
				}
				else if(name.equals("debug_AfterWriteNewUrlLine")){
					FileInfo file = (FileInfo) data;
					String writted = StreamUtil.streamToStr(new FileInputStream(file.f));
					String halfWritted = writted.substring(0, writted.length() - 6);
					file.raf.close();
					FileUtil.writeFileUTF8(file.f, halfWritted, false);
					file.raf = new RandomAccessFile(file.f, "rw");
					file.length = firstLength;
					throw new TestException();
				}
			}
		});
		try {
			db.addAsync(url1, comment1, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	
	
	
	
	
	
	
	//data will be rewrite
	@Test
	public void test_03_error_before_create_url_line() throws Exception {
		
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation03(dir1);
		afterCreateError01(db, dir1);
	
		File dir2 = new File(TEST_DIR, "2");
		situation03(dir2);
		afterCreateError02(dir2);

	}
	
	private CommentDbImpl situation03(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data) throws Exception {
				if(name.equals("debug_BeforeWriteNewUrlLine")) throw new TestException();
			}
		});
		try {
			db.addAsync(url1, comment1, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	
	
	
	
	
	
	//data will be rewrite
	@Test
	public void test_02_error_created_page_with_not_full_size() throws Exception {
		
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation02(dir1);
		afterCreateError01(db, dir1);
	
		File dir2 = new File(TEST_DIR, "2");
		situation02(dir2);
		afterCreateError02(dir2);

	}
	
	private CommentDbImpl situation02(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data)throws Exception {
				long firstLength = 0;
				if(name.equals("debug_BeforeWriteToPageFile")){
					FileInfo file = (FileInfo) data;
					firstLength = file.raf.getFilePointer();
				}
				else if(name.equals("debug_AfterWriteToPageFile")){
					FileInfo file = (FileInfo) data;
					String writted = StreamUtil.streamToStr(new FileInputStream(file.f));
					String halfWritted = writted.substring(0, writted.length() - 6);
					file.raf.close();
					FileUtil.writeFileUTF8(file.f, halfWritted, false);
					file.raf = new RandomAccessFile(file.f, "rw");
					file.length = firstLength;
					throw new TestException();
				}
			}
		});
		try {
			db.addAsync(url1, comment1, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	
	
	//no data
	@Test
	public void test_01_error_before_create_first_page() throws Exception {
		
		File dir1 = new File(TEST_DIR, "1");
		CommentDbImpl db = situation01(dir1);
		afterCreateError01(db, dir1);
	
		File dir2 = new File(TEST_DIR, "2");
		situation01(dir2);
		afterCreateError02(dir2);
		
	}
	
	private CommentDbImpl situation01(File dir) throws Exception {
		CommentDbImpl db = new CommentDbImpl(dir, new PageConverter(10, 2));
		DebugTool.add(new DebugListener() {
			@Override
			public void onEvent(String name, Object data) throws Exception {
				if(name.equals("debug_BeforeWriteToPageFile")) throw new TestException();
			}
		});
		try {
			db.addAsync(url1, comment1, 0).get();
			fail_exception_expected();
		}catch (ExecutionException e) {
			assertTestException(e);
		}
		DebugTool.clear();
		return db;
	}
	
	
	
	
	
	private void afterCreateError01(CommentDbImpl db, File dir) throws Exception{
		
		assertNull(db.getPageStream(url1, 0));
		db.addAsync(url2, comment3, 0).get();
		db.addAsync(url1, comment2, 0).get();
		assertEquals(comment2.getContent(), fromJson(db.getPageStream(url1, 0)).get(0).getContent());
		assertEquals(comment3.getContent(), fromJson(db.getPageStream(url2, 0)).get(0).getContent());
		
		//new db
		CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
		otherDb.addAsync(url1, comment4, 0).get();
		List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
		assertEquals(comment2.getContent(), list.get(0).getContent());
		assertEquals(comment4.getContent(), list.get(1).getContent());
		assertEquals(comment3.getContent(), fromJson(otherDb.getPageStream(url2, 0)).get(0).getContent());
	}
	
	
	private void afterCreateError02(File dir) throws Exception {
		//new db
		{
			CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
			assertNull(otherDb.getPageStream(url1, 0));
			otherDb.addAsync(url2, comment3, 0).get();
			otherDb.addAsync(url1, comment2, 0).get();
			otherDb.addAsync(url1, comment4, 0).get();
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(comment2.getContent(), list.get(0).getContent());
			assertEquals(comment4.getContent(), list.get(1).getContent());
			assertEquals(comment3.getContent(), fromJson(otherDb.getPageStream(url2, 0)).get(0).getContent());
		}
		//new db again
		{
			CommentDbImpl otherDb = new CommentDbImpl(dir, new PageConverter(10, 2));
			List<Comment> list = fromJson(otherDb.getPageStream(url1, 0));
			assertEquals(2, list.size());
			assertEquals(comment2.getContent(), list.get(0).getContent());
			assertEquals(comment4.getContent(), list.get(1).getContent());
			assertEquals(comment3.getContent(), fromJson(otherDb.getPageStream(url2, 0)).get(0).getContent());
		}
	}
	
	
	public static List<Comment> fromJson(InputStream is) throws IOException{
		return Comment.fromJsonStream(is);
	}
	
	public static void assertTestException(ExecutionException e) {
		assertEquals(TestException.class, e.getCause().getClass());
	}
	
}
