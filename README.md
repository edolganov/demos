# Demo projects
## Build and test
- Clone in Desktop
- Open in IDE (contains Eclipse project files)
- Every project has **test-src** folder with JUnit tests

## demo
Demo dir contains examples of work with all projects.
Run them in IDE.

## socket-server

**SocketServer** can be used for tcp socket communications:
```java
int port = 11002;
int maxThreads = 10;
SocketServer server = new SocketServer(port, maxThreads, new SocketWriterHander());
server.runAsync();
```

**SocketWriterHander** processes a connection and releases it for disconnecting:
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


## socket-client-pool
**SocketsPool** creates pooled tcp connections and can be used like socket's client:
```java
String host = "localhost";
int port = 11002;
SocketsPool pool = new SocketsPool(host, port);
pool.setPoolMaximumActiveConnections(10);
pool.setPoolMaximumIdleConnections(5);
```
**SocketConnHandler** processes a connection and returns it to the pool for other handlers:
```java
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
```

See [full example](https://github.com/edolganov/demos/blob/master/demo/src/SocketPool_Demo.java).