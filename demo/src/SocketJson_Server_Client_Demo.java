import demo.socket.json.client.JsonSocketClient;
import demo.socket.json.server.JsonSocketServer;
import demo.socket.json.server.ReqController;

import java.net.SocketAddress;
import java.util.concurrent.Future;


public class SocketJson_Server_Client_Demo {
	
	public static void main(String[] args) throws Exception {
		
		int port = 11001;
		int maxThreads = 10;
		String secureKey = "3rw!!esafd";
		
		
		JsonSocketServer server = new JsonSocketServer(port, maxThreads);
		server.setSecureKey(secureKey);
		server.runAsync();
		
		String host = "localhost";
		int maxConnections = 1;
		int idleConnections = 1;
		JsonSocketClient client = new JsonSocketClient(host, port, maxConnections, idleConnections);
		client.setSecureKey(secureKey);
		
		
		
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
			Future<Object> futureResult = client.invokeAsync(new Req("hello"));
			Resp resp = (Resp)futureResult.get();
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
