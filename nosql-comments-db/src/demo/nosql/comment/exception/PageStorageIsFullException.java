package demo.nosql.comment.exception;

import demo.exception.ExpectedException;


@SuppressWarnings("serial")
public class PageStorageIsFullException extends ExpectedException {

	public PageStorageIsFullException() {
		super();
	}

	public PageStorageIsFullException(String message, Throwable cause) {
		super(message, cause);
	}

	public PageStorageIsFullException(String message) {
		super(message);
	}

	public PageStorageIsFullException(Throwable cause) {
		super(cause);
	}
	
	
	
	
}
