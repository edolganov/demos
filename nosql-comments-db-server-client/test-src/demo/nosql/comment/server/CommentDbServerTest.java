package demo.nosql.comment.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

import demo.nosql.comment.common.AddCommentReq;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.server.CommentDbServer;
import demo.util.StreamUtil;
import demo.util.io.LimitInputStream;
import test.BaseTest;
import static demo.nosql.comment.common.RemoteApi.*;
import static demo.util.Util.*;
import static demo.util.json.GsonUtil.*;
import static demo.util.socket.SocketUtil.*;

public class CommentDbServerTest extends BaseTest {
	
	int port = 11001;
	int maxThreads = 10;
	
	
	@Test
	public void test_many_readers_writers_on_same_sockets() throws Exception {
		
		CommentDbServer server = new CommentDbServer(port, maxThreads, TEST_PATH);
		server.runAsync();
		
		try {
		
			String url = "ya.ru";
			
			Socket s = new Socket("localhost", port);
			s.setSoTimeout(1000);
			//add
			BufferedReader reader = getReaderUTF8(s);
			PrintWriter writer = getWriterUTF8(s);
			writer.println(ADD_COMMENT_ASYNC+defaultGson.toJson(new AddCommentReq(url, 1, "test")));
			assertEquals(OK, reader.readLine());
			writer = null;
			reader = null;
			
			
			BufferedReader reader2 = getReaderUTF8(s);
			PrintWriter writer2 = getWriterUTF8(s);
			writer2.println(ADD_COMMENT_ASYNC+defaultGson.toJson(new AddCommentReq(url, 1, "test")));
			assertEquals(OK, reader2.readLine());
		
		} finally {
			server.shutdownWait();			
		}
		
	}
	
	
	@Test
	public void test_add() throws Exception {
		
		CommentDbServer server = new CommentDbServer(port, maxThreads, TEST_PATH);
		server.runAsync();
		try {
		
			String url = "ya.ru";
			String comment = "привет1\r\n\t\0newLine";
			String jsonComment = "привет1\\n\\tnewLine";
			
			Socket s = new Socket("localhost", port);
			s.setSoTimeout(1000);
			InputStream is = s.getInputStream();
			BufferedReader reader = getReaderUTF8(is);
			PrintWriter writer = getWriterUTF8(s);
			writer.println(ADD_COMMENT_ASYNC+defaultGson.toJson(new AddCommentReq(url, 1, comment)));
			assertEquals(OK, reader.readLine());
			
			writer.println(GET_PAGE+"0-"+url);
			String resp = reader.readLine();
			assertTrue(resp, resp.startsWith(FILE));
			resp = reader.readLine();
			assertTrue(resp, resp.contains(jsonComment));
			
			s.close();
		} finally {
			server.shutdownWait();			
		}
		
		
	}
	
	
	@Test
	public void test_getPage() throws Exception{
		
		CommentDbServer server = new CommentDbServer(port, maxThreads, TEST_PATH);
		server.runAsync();
		
		try {
			//add by db
			String url = "ya.ru";
			String comment1 = "привет1\r\n\t\0newLine";
			String jsonComment1 = "привет1\\n\\tnewLine";
			server.getDb().addAsync(new Url(url), new Comment(1, comment1), 0).get();
			
			//test get
			Socket s = new Socket("localhost", port);
			s.setSoTimeout(1000);
			InputStream is = s.getInputStream();
			BufferedReader reader = getReaderUTF8(is);
			PrintWriter writer = getWriterUTF8(s);
			
			String resp = null;
			
			//invalid reqs
			{
				writer.println("test");
				resp = reader.readLine();
				assertTrue(resp, resp.startsWith(VALIDATION_ERROR));
				
				writer.println(GET_PAGE);
				resp = reader.readLine();
				assertTrue(resp, resp.startsWith(VALIDATION_ERROR));
				
				writer.println(GET_PAGE+url);
				resp = reader.readLine();
				assertTrue(resp, resp.startsWith(VALIDATION_ERROR));
				
				writer.println(GET_PAGE+"-"+url);
				resp = reader.readLine();
				assertTrue(resp, resp.startsWith(VALIDATION_ERROR));
				
				writer.println(GET_PAGE+"0");
				resp = reader.readLine();
				assertTrue(resp, resp.startsWith(VALIDATION_ERROR));
				
				writer.println(GET_PAGE+"0-");
				resp = reader.readLine();
				assertTrue(resp, resp.startsWith(VALIDATION_ERROR));
			}
			
			//valid reqs
			writer.println(GET_PAGE+"0-"+url+"unknown");
			resp = reader.readLine();
			assertEquals(NULL, resp);
			
			writer.println(GET_PAGE+"0-"+url);
			resp = reader.readLine();
			assertTrue(resp, resp.startsWith(FILE));
			int contentLength = tryParseInt(resp.substring(FILE.length()), -1);
			assertTrue(contentLength > 0);
			resp = reader.readLine();
			assertTrue(resp, resp.contains(jsonComment1));
			
			
			//2 comments
			String comment2 = "dslkfjsdalfjads;lkfjsdl;fkjsdalfjsadl;jflsdajflks";
			String jsonComment2 = comment2;
			server.getDb().addAsync(new Url(url), new Comment(1, comment2), 0).get();
			
			writer.println(GET_PAGE+"0-"+url);
			resp = reader.readLine();
			resp = reader.readLine();
			assertTrue(resp, resp.contains(jsonComment1) && resp.contains(jsonComment2));
			String pageJson = resp;
			s.close();
			
			
			//read stream
			//can't use Reader and InputStream вместе
			s = new Socket("localhost", port);
			s.setSoTimeout(1000);
			is = s.getInputStream();
			reader = getReaderUTF8(is);
			writer = getWriterUTF8(s);
			
			writer.println(GET_PAGE+"0-"+url);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int byteVal;
			while(true){
				byteVal = is.read();
				if(byteVal == -1 || byteVal == '\n') break;
				baos.write(byteVal);
			}
			resp = baos.toString("UTF-8");
			contentLength = tryParseInt(resp.substring(FILE.length()), -1);
			assertTrue(contentLength > 0);
			
			baos = new ByteArrayOutputStream();
			LimitInputStream lis = new LimitInputStream(is, contentLength-1);
			StreamUtil.copy(lis, baos, false);
			String pageJsonByStream = baos.toString("UTF-8");
			assertEquals(pageJson, pageJsonByStream);
			assertEquals('\n', is.read());
			
			s.close();
		
		} finally {
			server.shutdownWait();			
		}
		
		
		
	}



}
