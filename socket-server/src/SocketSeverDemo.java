import static demo.util.Util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import demo.socket.server.SocketServer;
import demo.socket.server.SocketWriterHander;



public class SocketSeverDemo {
	
	public static void main(String[] args) throws IOException {
		
		int port = 11002;
		int maxThreads = 10;
		SocketServer server = new SocketServer(port, maxThreads, new EchoHandler());
		
		System.out.println("Server started on port: " + 11002+". Try to connect by http://127.0.0.1:"+port);
		server.runWait();
		
	}
	
	/** Handler example */
	static class EchoHandler extends SocketWriterHander {
		
		@Override
		protected void process(Socket openedSocket, BufferedReader socketReader, PrintWriter socketWriter, SocketServer owner) throws Throwable {
			
			String echo = "";
			
			String line = socketReader.readLine();
			while( hasText(line)){
				echo += line + "\n";
				line = socketReader.readLine();
			}
			
			socketWriter.println("SocketSever. Echo example:\n"+echo);
			socketWriter.flush();
		}
	}

}
