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
			server.putController(Req.class, new ReqController<Req, Resp>() {
				@Override
				public Resp processReq(Req data, SocketAddress remoteAddress) throws Exception {
					
					System.out.println("Client req: " + data);
					
					Resp resp = new Resp("echo: "+data.in);
					return resp;
				}
			});
			
			//client req
			Resp resp = (Resp)client.invoke(new Req("hello"));
			System.out.println("Server resp: " + resp);
			
			client.forceCloseAll();
			
		}finally {
			server.shutdownAsync();
		}
		
	}
	
	static class Req {
		
		public String in;

		public Req() {
		}

		public Req(String in) {
			this.in = in;
		}

		@Override
		public String toString() {
			return "Req [in=" + in + "]";
		}
		
	}
	
	static class Resp {
		
		public String result;

		public Resp() {
		}

		public Resp(String result) {
			this.result = result;
		}

		@Override
		public String toString() {
			return "Resp [result=" + result + "]";
		}
		
		
	}

}
