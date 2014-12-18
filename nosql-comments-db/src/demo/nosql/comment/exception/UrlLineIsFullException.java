package demo.nosql.comment.exception;

import demo.exception.ExpectedException;

@SuppressWarnings("serial")
public class UrlLineIsFullException extends ExpectedException {

	public UrlLineIsFullException() {
		super();
	}

	public UrlLineIsFullException(String message, Throwable cause) {
		super(message, cause);
	}

	public UrlLineIsFullException(String message) {
		super(message);
	}

	public UrlLineIsFullException(Throwable cause) {
		super(cause);
	}
	
	
	
	
}
