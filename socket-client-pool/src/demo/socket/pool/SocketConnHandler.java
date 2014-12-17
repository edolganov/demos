package demo.socket.pool;

import java.io.IOException;

import static demo.util.ExceptionUtil.*;

public abstract class SocketConnHandler<T> {
	
	public abstract T handle(SocketConn c) throws IOException;
	
	public T onException(SocketConn c, Throwable t) throws IOException {
		if(c != null) c.invalidate();
		throw unwrapIOException(t);
	}
}
