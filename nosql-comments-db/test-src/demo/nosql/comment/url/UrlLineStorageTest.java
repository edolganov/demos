package demo.nosql.comment.url;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

import demo.exception.io.BrokenFileException;
import demo.nosql.comment.exception.UrlLineIsFullException;
import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.model.UrlLine;
import demo.nosql.comment.url.UrlLineConverter;
import demo.nosql.comment.url.UrlLineReader;
import demo.nosql.comment.url.UrlLineStorage;
import demo.util.FileUtil;
import demo.util.StringUtil;
import test.BaseTest;
import static demo.model.GlobalConst.*;
import static demo.nosql.comment.url.UrlLineConverter.*;

public class UrlLineStorageTest extends BaseTest {
	
	
	@Test
	public void test_full_page() throws IOException {
		
		UrlLineStorage storage = new UrlLineStorage(TEST_DIR);
		long offset = storage.createLine(0, "123", new CommentsPage(1, 2));
		for (int i = 1; i < MAX_PAGES_COUNT; i++) {
			assertFalse(storage.isFullLine(i));
			storage.updateLine(offset, i, new CommentsPage(i+1, i+2));
		}
		
		UrlLineReader reader = storage.readLines();
		UrlLine line = reader.next();
		reader.close();
		
		assertEquals(MAX_PAGES_COUNT, line.pages.length);
		for (int i = 0; i < MAX_PAGES_COUNT; i++) {
			assertEquals(i+1, line.pages[i].virtualOffset);
			assertEquals(i+2, line.pages[i].limit);
		}
		
		try {
			assertTrue(storage.isFullLine(MAX_PAGES_COUNT));
			storage.updateLine(offset, MAX_PAGES_COUNT, new CommentsPage(1, 2));
			fail_exception_expected();
		}catch (UrlLineIsFullException e) {
			//ok
		}
		
	}
	
	
	@Test
	public void test_broken_file() throws IOException {
		
		
		//broken in start
		{
			File testDir = new File(TEST_DIR, "test1");
			testDir.mkdir();
			File file = new File(testDir, UrlLineStorage.FILE_NAME);
			file.createNewFile();
			FileUtil.writeFileUTF8(file, "broken!");
			
			UrlLineStorage storage = new UrlLineStorage(testDir);
			try {
				storage.readLines().next();
				fail_exception_expected();
			}catch (BrokenFileException e) {
				//ok
			}
		}
		
		//broken in end
		{
			File testDir = new File(TEST_DIR, "test2");
			testDir.mkdir();
			File file = new File(testDir, UrlLineStorage.FILE_NAME);
			
			UrlLineStorage storage = new UrlLineStorage(testDir);
			storage.createLine(0, "123", new CommentsPage(11, 22));
			FileUtil.writeFileUTF8(file, "broken!", true);
			UrlLineReader reader = storage.readLines();
			reader.next();
			try {
				reader.next();
				fail_exception_expected();
			}catch (BrokenFileException e) {
				//ok
			}
		}
		
		
		//broken in middle
		test_Broken_in_middle("test3", GLOBAL_ORDER_BYTES_COUNT-1);
		test_Broken_in_middle("test4", GLOBAL_ORDER_BYTES_COUNT+URL_BYTES_COUNT-1);
		test_Broken_in_middle("test5", LINE_BYTES_COUNT-1);
		
	}

	private void test_Broken_in_middle(String dirName, long offset) throws IOException {
		
		File testDir = new File(TEST_DIR, dirName);
		testDir.mkdir();
		File file = new File(testDir, UrlLineStorage.FILE_NAME);
		
		UrlLineStorage storage = new UrlLineStorage(testDir);
		storage.createLine(0, "123", new CommentsPage(11, 22));
		
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.seek(offset);
		raf.writeChar('@');
		raf.close();
		
		UrlLineReader reader = storage.readLines();
		try {
			reader.next();
			fail_exception_expected();
		}catch (BrokenFileException e) {
			//ok
		}
	}
	
	@Test
	public void test_empty_file() throws IOException {
		
		UrlLineStorage storage = new UrlLineStorage(TEST_DIR);
		UrlLineReader reader = storage.readLines();
		assertNull(reader.next());
		assertNull(reader.next());
		
	}
	
	
	
	
	@Test
	public void test_create_max_url() throws IOException{
		
		String partToWrite = StringUtil.createStr('я', Url.MAX_URL_SIZE);
		String url = partToWrite + "-extra-part";
		
		UrlLineStorage storage = new UrlLineStorage(TEST_DIR);
		storage.createLine(0, url, new CommentsPage(11, 22));
		
		assertEquals(partToWrite, storage.readLines().next().url);
	}
	
	
	
	
	
	@Test
	public void test_create_read_update() throws IOException{
		
		UrlLineStorage storage = new UrlLineStorage(TEST_DIR);
		
		//create
		String url1 = "ya.ru";
		long offset1 = storage.createLine(0, url1, new CommentsPage(11, 22));
		assertEquals(0, offset1);
		
		storage.updateLine(offset1, 1, new CommentsPage(33, 44));
		
		long offset2 = storage.createLine(1, "гугл.рф", new CommentsPage(55, 66));
		assertEquals(UrlLineConverter.LINE_BYTES_COUNT, offset2);
		
		
		
		//read
		UrlLineReader reader = storage.readLines();
		UrlLine result1 = reader.next();
		assertEquals("ya.ru", result1.url);
		assertEquals(offset1, result1.offset);
		assertEquals(0, result1.pageBlockIndex);
		assertEquals(2, result1.pages.length);
		assertEquals(11, result1.pages[0].virtualOffset);
		assertEquals(22, result1.pages[0].limit);
		assertEquals(33, result1.pages[1].virtualOffset);
		assertEquals(44, result1.pages[1].limit);
		result1 = null;
		
		UrlLine result2 = reader.next();
		assertEquals("гугл.рф", result2.url);
		assertEquals(offset2, result2.offset);
		assertEquals(1, result2.pageBlockIndex);
		assertEquals(1, result2.pages.length);
		assertEquals(55, result2.pages[0].virtualOffset);
		assertEquals(66, result2.pages[0].limit);
		result2 = null;
		
		assertNull(reader.next());
		reader.close();
		reader = null;
		
		
		//update
		storage.updateLine(offset1, 0, new CommentsPage(111, 222));
		storage.updateLine(offset1, 1, new CommentsPage(333, 444));
		storage.updateLine(offset2, 0, new CommentsPage(555, 666));
		
		
		//read again
		UrlLineReader reader2 = storage.readLines();
		UrlLine result3 = reader2.next();
		assertEquals("ya.ru", result3.url);
		assertEquals(offset1, result3.offset);
		assertEquals(0, result3.pageBlockIndex);
		assertEquals(2, result3.pages.length);
		assertEquals(111, result3.pages[0].virtualOffset);
		assertEquals(222, result3.pages[0].limit);
		assertEquals(333, result3.pages[1].virtualOffset);
		assertEquals(444, result3.pages[1].limit);
		result3 = null;
		
		UrlLine result4 = reader2.next();
		assertEquals("гугл.рф", result4.url);
		assertEquals(offset2, result4.offset);
		assertEquals(1, result4.pageBlockIndex);
		assertEquals(1, result4.pages.length);
		assertEquals(555, result4.pages[0].virtualOffset);
		assertEquals(666, result4.pages[0].limit);
		result4 = null;
		
		assertNull(reader2.next());
		reader2.close();
		reader2 = null;
	}

}
