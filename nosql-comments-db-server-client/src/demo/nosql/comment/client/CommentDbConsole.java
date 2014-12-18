package demo.nosql.comment.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import demo.util.StreamUtil;

import static demo.util.Util.*;

public class CommentDbConsole {
	
	 static {
		 System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	 }
	
	public static void main(String[] args) throws IOException {
		
		String host = "localhost";
		if(args.length > 0) host = args[0];
		
		int port = 12060;
		if(args.length > 1) port = tryParseInt(args[1], port);
		
		new CommentDbConsole(host, port).run();
	}
	
	
	String host;
	int port;
	PrintStream out;
	BufferedReader reader;
	
	public CommentDbConsole(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		out = System.out;
	}

	public void run() throws IOException{
		

		println();
		out.println("-----------------------------------");
		out.println("Welcome to CommentDb Console (host:"+host+", port:"+port+")");
		out.println("-----------------------------------");
		println();
		
		CommentDbClient client = new CommentDbClient(host, port, 1, 1, null);
		//test conn
		client.getUrlsCount();
		
		println();
		println("For help type '?' and press Enter");
		out.print("> ");
		
		reader = StreamUtil.getReaderUTF8(System.in);
		while(true){
			
			try {
				
				
				String command = reader.readLine();
				if(command == null) command = "";
				
				if(command.equals("exit")) break;
				
				else if(command.equals("?")){
					println("\t'exit' - exit program");
					println("\t'connections' - cur connections count");
					println("\t'backup [dir path]' - create data backup");
					println("\t'urlsSnapshot [file path]' - create urls state shapshot");
				}
				
				else if(command.equals("connections")){
					println(client.connectionsCount());
				}
				else if(command.startsWith("urlsSnapshot")){
					int paramIndex = "urlsSnapshot".length()+1;
					String destPath = command.length() > paramIndex? command.substring(paramIndex) : null;
					client.createUrlsStateSnapshot(destPath);
					printlnDone();
				}
				else if(command.startsWith("backup")){
					int paramIndex = "backup".length()+1;
					String destPath = command.length() > paramIndex? command.substring(paramIndex) : null;
					client.createBackup(destPath);
					printlnDone();
				}
				
			} catch (Exception e) {
				println("can't invoke command: "+e);
			}
			
			out.print("> ");
		}
	}

	void println() {
		out.println();
	}
	
	void printlnDone() {
		println("done");
	}
	
	void println(Object ob) {
		out.println(" "+String.valueOf(ob));
	}
	
	void print(Object ob) {
		out.print(" "+String.valueOf(ob));
	}

}
