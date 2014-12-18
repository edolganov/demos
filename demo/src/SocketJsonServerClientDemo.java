import java.io.IOException;
import java.net.SocketAddress;

import demo.socket.json.client.JsonSocketClient;
import demo.socket.json.server.JsonSocketServer;
import demo.socket.json.server.ReqController;


public class SocketJsonServerClientDemo {
	
	public static void main(String[] args) throws IOException {
		
		String host = "localhost";
		int port = 11001;
		int maxThreads = 10;
		String encodeContentKey = "3rw!!esafd";
		
		
		JsonSocketServer server = new JsonSocketServer(port, maxThreads);
		JsonSocketClient client = new JsonSocketClient(host, port, 1, 1);
		
		server.setSecureKey(encodeContentKey);
		client.setSecureKey(encodeContentKey);
		
		server.runAsync();
		
		try {
			
			//server handler
			server.putController(ReqObject.class, new ReqController<ReqObject, RespObject>() {
				@Override
				public RespObject processReq(ReqObject data, SocketAddress remoteAddress) throws Exception {
					
					System.out.println("Client req: " + data);
					
					RespObject resp = new RespObject("echo: "+data.in);
					return resp;
				}
			});
			
			//client req
			RespObject resp = (RespObject)client.invoke(new ReqObject("hello"));
			System.out.println("Server resp: " + resp);
			
		}finally {
			server.shutdownWait();
		}
		
	}
	
	static class ReqObject {
		
		public String in;

		public ReqObject() {
		}

		public ReqObject(String in) {
			this.in = in;
		}

		@Override
		public String toString() {
			return "ReqObject [in=" + in + "]";
		}
		
	}
	
	static class RespObject {
		
		public String result;

		public RespObject() {
		}

		public RespObject(String result) {
			this.result = result;
		}

		@Override
		public String toString() {
			return "RespObject [result=" + result + "]";
		}
		
		
	}

}
