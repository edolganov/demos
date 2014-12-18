package sc.comp.cdb.client;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import demo.nosql.comment.client.CommentDbClient;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentId;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.server.CommentDbServer;
import demo.util.StreamUtil;
import demo.util.io.LimitInputStream;
import test.BaseTest;
import static demo.util.Util.*;

public class CommentDbClientTest extends BaseTest {
	
	int port = 11001;
	int maxThreads = 10;
	
	@Test
	public void test_all() throws Exception{
		
		String url = "ya.ru";
		
		CommentDbServer server = new CommentDbServer(port, maxThreads, TEST_PATH);
		server.runAsync();
		
		CommentDbClient client = new CommentDbClient("localhost", port, 5, 5);
		//add
		client.addAsync(new Url(url), new Comment("test1"), 0).get();
		client.addAsync(new Url(url), new Comment("test2"), 0).get();
		//read
		LimitInputStream pageStream = client.getPageStream(new Url(url), 0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamUtil.copy(pageStream, baos, true);
		String page = baos.toString();
		assertTrue(page, page.contains("test1") && page.contains("test2"));
		
		assertEquals("test1", client.getComment(new CommentId(new Url(url), 0, 0)).getContent());
		assertEquals("test2", client.getComment(new CommentId(new Url(url), 0, 1)).getContent());
		assertNull(client.getComment(new CommentId(new Url(url), 0, 2)));
		
		//other
		assertEquals(1, client.getAllCommentsPagesCount());
		assertEquals(1, client.getCommentsPagesCount(new Url(url)));
		assertEquals(1, client.getUrlsCount());
		assertEquals(list(url), toList(client.getUrlsEnumeration()));
		assertFalse(client.isFull());
		
		client.createUrlsStateSnapshot(TEST_PATH+"/test.BAK");
		client.createUrlsStateSnapshot(null);
		
		client.createBackup(TEST_PATH+"/backup-dir");
		client.createBackup(null);
		
		server.shutdownWait();
		
	}

}
