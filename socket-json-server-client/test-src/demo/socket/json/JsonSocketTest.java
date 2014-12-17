package demo.socket.json;


import demo.junit.AssertExt;
import demo.socket.json.client.JsonSocketClient;
import demo.socket.json.common.InvalidRespException;
import demo.socket.json.common.ValidationException;
import demo.socket.json.server.JsonSocketServer;
import demo.socket.json.server.ReqController;

import java.io.IOException;
import java.net.SocketAddress;

import org.junit.Test;


public class JsonSocketTest extends AssertExt {
	
	String host = "localhost";
	int port = 11001;
	int maxThreads = 10;
	
	
	@Test
	public void test_change_secure_key() throws IOException{
		
		JsonSocketServer server = new JsonSocketServer(port, maxThreads);
		JsonSocketClient client = new JsonSocketClient(host, port, 1, 1);
		
		String key1 = "djdjda";
		String key2 = "asdf333";
		server.setSecureKey(key1);
		client.setSecureKey(key1);
		
		server.runAsync();

		try {
			
			server.putController(String.class, new ReqController<String, String>() {
				@Override
				public String processReq(String data, SocketAddress remoteAddress) throws Exception {
					return "echo:"+data;
				}
			});
			
			assertEquals("echo:hello", client.invoke("hello"));
			
			
			server.setSecureKey(key2);
			try {
				client.invoke("hello");
				fail_exception_expected();
			}catch(InvalidRespException e){
				//ok
			}
			
			
			client.setSecureKey(key2);
			assertEquals("echo:hello", client.invoke("hello"));
			
		} finally {
			server.shutdownWait();
		}
	}
	
	
	@Test
	public void test_secured() throws Exception {
		
		JsonSocketServer server = new JsonSocketServer(port, maxThreads);
		JsonSocketClient client = new JsonSocketClient(host, port, 1, 1);
		
		String key = "djdjda";
		
		server.setSecureKey(key);
		client.setSecureKey(key);
		
		baseClientServerTest(server, client);
	}
	
	
	@Test
	public void test_unsecure() throws Exception{
		
		JsonSocketServer server = new JsonSocketServer(port, maxThreads);
		JsonSocketClient client = new JsonSocketClient(host, port, 1, 1);
		
		baseClientServerTest(server, client);
	}
	
	private void baseClientServerTest(JsonSocketServer server, JsonSocketClient client) throws Exception{
		
		server.runAsync();
		
		try {
			
			//no controller
			{
				try {
					 client.invoke("hello");
					 fail_exception_expected();
				}catch (ValidationException e) {
					//ok
				}
			}
			
			
			//put controller
			{
				server.putController(String.class, new ReqController<String, String>() {
					@Override
					public String processReq(String data, SocketAddress remoteAddress) throws Exception {
						return "echo:"+data;
					}
				});
				assertEquals("echo:hello", client.invoke("hello"));
			}
			
			
			//remove controller
			{
				server.removeController(String.class);
				try {
					 client.invoke("hello");
					 fail_exception_expected();
				}catch (ValidationException e) {
					//ok
				}
			}
			
			
			//null req
			{
				server.putController(String.class, new ReqController<String, String>() {
					@Override
					public String processReq(String data, SocketAddress remoteAddress) throws Exception {
						return "echo:"+data;
					}
				});
				assertEquals("echo:null", client.invoke(String.class, null));
			}
			
			
			//null resp
			{
				server.putController(String.class, new ReqController<String, String>() {
					@Override
					public String processReq(String data, SocketAddress remoteAddress) throws Exception {
						return null;
					}
				});
				assertNull(client.invoke("hello"));
			}
			
			
			//async
			{
				server.putController(String.class, new ReqController<String, String>() {
					@Override
					public String processReq(String data, SocketAddress remoteAddress) throws Exception {
						return "echo:"+data;
					}
				});
				assertEquals("echo:hello", client.invokeAsync("hello").get());
			}
			
			
			//void
			{
				server.putController(String.class, new ReqController<String, Void>() {
					@Override
					public Void processReq(String data, SocketAddress remoteAddress) throws Exception {
						return null;
					}
				});
				assertNull(client.invoke("hello"));
			}
			
			
		} finally {
			server.shutdownWait();
		}
		
		
		
	}

}
