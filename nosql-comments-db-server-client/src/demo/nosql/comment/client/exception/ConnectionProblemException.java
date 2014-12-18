package demo.nosql.comment.client.exception;

import demo.exception.ExpectedException;

@SuppressWarnings("serial")
public class ConnectionProblemException extends ExpectedException {
	
	public final String host;
	public final int port;
	
	public ConnectionProblemException(String host, int port, Throwable t) {
		super("host="+host+", port="+port+", cause="+t, t);
		this.host = host;
		this.port = port;
	}

}
