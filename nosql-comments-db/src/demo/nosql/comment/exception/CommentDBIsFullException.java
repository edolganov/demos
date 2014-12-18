package demo.nosql.comment.exception;

import demo.exception.ExpectedException;

@SuppressWarnings("serial")
public class CommentDBIsFullException extends ExpectedException {

	public CommentDBIsFullException() {
		super();
	}

	public CommentDBIsFullException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommentDBIsFullException(String message) {
		super(message);
	}

	public CommentDBIsFullException(Throwable cause) {
		super(cause);
	}
	
	
	
	
}
