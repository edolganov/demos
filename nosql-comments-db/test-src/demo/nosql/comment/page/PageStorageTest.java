package demo.nosql.comment.page;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import demo.nosql.comment.exception.PageStorageIsFullException;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.page.PageConverter;
import demo.nosql.comment.page.PageStorage;
import test.BaseTest;
import static demo.nosql.comment.model.Comment.*;
import static demo.nosql.comment.page.PageConverter.*;
import static demo.nosql.comment.page.PageStorage.*;
import static demo.util.NumberUtil.*;
import static demo.util.StreamUtil.*;
import static demo.util.StringUtil.*;
import static demo.util.json.GsonUtil.*;

public class PageStorageTest extends BaseTest {
	
	
	@Test
	public void test_max_pages_count()throws Exception{
		
		int pagesInFile = 2;
		PageStorage storage = new PageStorage(TEST_DIR, new PageConverter(DEFAULT_COMMENTS_IN_PAGE, pagesInFile));
		
		for (int i = 0; i < DEFAULT_MAX_FILES_COUNT; i++) {
			for (int j = 0; j < pagesInFile; j++) {
				assertFalse(storage.isFull());
				storage.createPage(new Comment(i+"-"+j));
			}
		}
		
		try {
			assertTrue(storage.isFull());
			storage.createPage(new Comment("over"));
			fail_exception_expected();
		}catch (PageStorageIsFullException e) {
			//ok
		}
	}
	
	
	
	@Test
	public void test_create_virtual_offset()throws Exception{
	
			
			int pagesInFile = 2;

			File file2 = new File(TEST_DIR, getFileName(1));
			File file3 = new File(TEST_DIR, getFileName(2));
			
			PageStorage storage = new PageStorage(TEST_DIR, new PageConverter(DEFAULT_COMMENTS_IN_PAGE, pagesInFile), DEFAULT_MAX_FILES_COUNT);
			for (int i = 0; i < pagesInFile; i++) {
				storage.createPage(new Comment(""+i));
			}
			assertFalse(file2.exists());
			
			//create
			String content1 = "new file 2";
			String content2 = "new file 2.1";
			String content3 = "new file 3";
			
			CommentsPage p2 = storage.createPage(new Comment(content1));
			assertTrue(file2.exists());
			
			//read
			assertEquals(content1, getComments(storage, p2).get(0).getContent());
			
			//update
			p2 = storage.updateOrCreatePage(p2, new Comment(content2));
			assertEquals(content1, getComments(storage, p2).get(0).getContent());
			assertEquals(content2, getComments(storage, p2).get(1).getContent());
			
			

			storage.createPage(new Comment(""));
			assertFalse(file3.exists());
			
			CommentsPage p3 = storage.createPage(new Comment(content3));
			assertTrue(file3.exists());
			assertEquals(content3, getComments(storage, p3).get(0).getContent());
			
	}
	
	@Test
	public void test_virtual_offset(){
		
		int fileSize = 1000;
		int filesCount = 3;
		
		int realOffset = 15;
		int virtualOffset = fileSize * filesCount + realOffset;
		
		assertEquals(realOffset, virtualOffset % fileSize);
		assertEquals(filesCount, virtualOffset / fileSize);
	}
	
	
	@Test
	public void test_create_max_page_with_min_content()throws IOException{

		//write
		PageStorage storage = new PageStorage(TEST_DIR);
		
		CommentsPage p = storage.createPage(new Comment(""));
		
		for (int i = 1; i < storage.c.commentsInPage; i++) {
			CommentsPage oldP = p;
			p = storage.updateOrCreatePage(oldP, new Comment(""));
		}
		
		CommentsPage oldP = p;
		p = storage.updateOrCreatePage(oldP, new Comment(""));
		assertEquals(oldP.virtualOffset, p.virtualOffset);
		
	}
	
	
	
	@Test
	public void test_create_max_page_with_max_content()throws IOException{
		
		PageConverter defaultConverter = new PageConverter();
		assertEquals(defaultConverter.commentsInPage, (defaultConverter.pageBytesCount / defaultConverter.commentBytesCount));
		
		
		//write
		File pagesFile = new File(TEST_DIR, getFileName(0));
		PageStorage storage = new PageStorage(TEST_DIR);
		PageConverter c = storage.c;
		
		String maxStr = createStr('я', CONTENT_MAX_SIZE-3);
		CommentsPage p = storage.createPage(new Comment(new Date(), Long.MAX_VALUE, "00_"+maxStr));
		assertEquals(c.pageBytesCount, pagesFile.length());
		
		for (int i = 1; i < storage.c.commentsInPage; i++) {
			
			CommentsPage oldP = p;
			String curContent = zeroFormattedStr(i, 2)+"_"+maxStr;
			p = storage.updateOrCreatePage(oldP, new Comment(new Date(), Long.MAX_VALUE, curContent));
			List<Comment> list = getComments(storage, p);
			
			assertEquals(oldP.virtualOffset, p.virtualOffset);
			assertEquals(curContent, list.get(list.size()-1).getContent());
		}
		
		CommentsPage oldP = p;
		String curContent = c.commentsInPage+"_"+maxStr;
		p = storage.updateOrCreatePage(oldP, new Comment(new Date(), Long.MAX_VALUE, curContent));
		List<Comment> list = getComments(storage, p);

		assertEquals((long)(oldP.virtualOffset + c.pageBytesCount), p.virtualOffset);
		assertEquals(curContent, list.get(list.size()-1).getContent());
		assertEquals(c.pageBytesCount*2, pagesFile.length());
		

		
	}
	
	
	@Test
	public void test_create_read_append() throws IOException{
		
		String content1 = "123";
		String content2 = createStr('я', CONTENT_MAX_SIZE);
		String content3 = createStr('z', CONTENT_MAX_SIZE);
		String content4 = "абв\n\t</>\"";
		
		PageStorage storage = new PageStorage(TEST_DIR);
		PageConverter c = storage.c;
		
		//create
		CommentsPage p1 = storage.createPage(new Comment(content1));
		assertEquals(0, p1.virtualOffset);
		
		CommentsPage p2 = storage.createPage(new Comment(content2));
		assertEquals(c.pageBytesCount, p2.virtualOffset);
		
		
		//read
		assertEquals(content1, getComments(storage, p1).get(0).getContent());
		assertEquals(content2, getComments(storage, p2).get(0).getContent());
		
		
		//update
		p1 = storage.updateOrCreatePage(p1, new Comment(content3));
		List<Comment> list1 = getComments(storage, p1);
		assertEquals(content1, list1.get(0).getContent());
		assertEquals(content3, list1.get(1).getContent());
		
		p2 = storage.updateOrCreatePage(p2, new Comment(content4));
		List<Comment> list2 = getComments(storage, p2);
		assertEquals(content2, list2.get(0).getContent());
		assertEquals(content4, list2.get(1).getContent());
		
		storage.close();
		storage = null;
		
		
		//read again
		storage = new PageStorage(TEST_DIR);
		list1 = getComments(storage, p1);
		assertEquals(content1, list1.get(0).getContent());
		assertEquals(content3, list1.get(1).getContent());
		list2 = getComments(storage, p2);
		assertEquals(content2, list2.get(0).getContent());
		assertEquals(content4, list2.get(1).getContent());
		
	}

	private List<Comment> getComments(PageStorage storage, CommentsPage p) throws IOException {
		String json = streamToStr(storage.getPageStream(p.virtualOffset, p.limit));
		assertEquals('{', json.charAt(0));
		assertEquals('}', json.charAt(json.length()-1));
		return getList("["+json+"]", Comment.class, defaultGson);
	}

}
