# Demo projects
## Build and test
- Clone in Desktop
- Open in IDE (contains Eclipse project files)
- Every project has **test-src** folder with JUnit tests

## /demo
Demo dir contains examples of work with all projects.
Run them in IDE.

## /socket-server

Socket server can be used for sockets communications:
```java
int port = 11002;
int maxThreads = 10;
SocketServer server = new SocketServer(port, maxThreads, new SocketWriterHander());
server.runAsync();
```

SocketWriterHander process every socket connection and release it for disconnecting:
```java
public static class HttpEchoHandler extends SocketWriterHander {
	
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
```

See [full example](https://github.com/edolganov/demos/blob/master/demo/src/SocketSever_Demo.java).