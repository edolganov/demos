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


## socket-json-server-client
**JsonSocketServer** is extension of [SocketServer](https://github.com/edolganov/demos#socket-server) 
with JSON protocol for java objects serialization and with AES128 encryption:
```java
int port = 11001;
int maxThreads = 10;
String secureKey = "3rw!!esafd";

JsonSocketServer server = new JsonSocketServer(port, maxThreads);
server.setSecureKey(secureKey);
server.runAsync();

server.putController(Req.class, new ReqController<Req, Resp>() {
	@Override
	public Resp processReq(Req data, SocketAddress remoteAddress) throws Exception {
		
		System.out.println("Client req: " + data);
		
		Resp resp = new Resp("echo: "+data.in);
		return resp;
	}
});
```
**JsonSocketClient** uses [SocketsPool](https://github.com/edolganov/demos#socket-client-pool) and has easy 'request-response' api:
```java
String host = "localhost";
int port = 11001;
int maxConnections = 10;
int idleConnections = 5;
String secureKey = "3rw!!esafd";

JsonSocketClient client = new JsonSocketClient(host, port, maxConnections, idleConnections);
client.setSecureKey(secureKey);

//request - response
Future<Object> futureResult = client.invokeAsync(new Req("hello"));
Resp resp = (Resp)futureResult.get();
```

See [full example](https://github.com/edolganov/demos/blob/master/demo/src/SocketJson_Server_Client_Demo.java).

## nosql-comments-db
NoSql database for storing a comments in JSON format and fast read them.

**Capabilities:** 
- Creating 10.000 urls with 500 comments (5.000.0000 total comments count)
- 40MB RAM
- 3,2GB HDD

See [the test](https://github.com/edolganov/demos/blob/master/nosql-comments-db/test-src/performance/MaxDBCreate.java).

This database was created and used in [freecom.me](http://freecom.me/) project.

See [example of work](https://github.com/edolganov/demos/blob/master/demo/src/NoSql_CommentsDB_Demo.java).