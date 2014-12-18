import static demo.util.Util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import demo.socket.pool.SocketConn;
import demo.socket.pool.SocketConnHandler;
import demo.socket.pool.SocketsPool;
import demo.socket.server.SocketServer;
import demo.socket.server.SocketWriterHander;
import demo.util.StreamUtil;


public class SocketPoolDemo {
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader inputReader = StreamUtil.getReaderUTF8(System.in);
		
		
		
		String host = "localhost";
		int port = 11001;
		int maxThreads = 10;
		
		//run echo server
		SocketServer server = new SocketServer(port, maxThreads, new ReverseEchoHandler());
		server.runAsync();
		
		

		
		//create and use client pool
		try {
			
			SocketsPool pool = new SocketsPool(host, port);
			pool.setPoolMaximumActiveConnections(1);
			pool.setPoolMaximumIdleConnections(1);
			
			System.out.println("We created pool to server: " + host + ":"+ port);
			System.out.println("Type request to server:");
			
			String line = inputReader.readLine();
			while( ! "exit".equals(line)){
				
				if( ! hasText(line)) {
					System.out.println("Type some text to server or 'exit' to end the demo");
					line = inputReader.readLine();
					continue;
				}
				
				final String msg = line;
				
				//use sockets pool to send msg to server and get response
				String answer = pool.invoke(new SocketConnHandler<String>() {
					@Override
					public String handle(SocketConn c) throws IOException {
						
						PrintWriter writer = c.getWriter();
						writer.println(msg);
						writer.flush();
						
						BufferedReader reader = c.getReader();
						return reader.readLine();
					}
				});
				System.out.println("SERVER: " + answer);
				
				
				line = inputReader.readLine();
			}
			
			pool.forceCloseAll();
			
			
		} finally {
			server.shutdownWait();
		}
		
	}
	
	
	/** Socket connection handler example */
	public static class ReverseEchoHandler extends SocketWriterHander {
		
		@Override
		protected void process(Socket openedSocket, BufferedReader socketReader, PrintWriter socketWriter, SocketServer owner) throws Throwable {
			
			String line = socketReader.readLine();
			while( hasText(line)){
				
				socketWriter.println(new StringBuilder(line).reverse());
				socketWriter.flush();
				
				line = socketReader.readLine();
			}
		}
	}

}
