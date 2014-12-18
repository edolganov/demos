package demo.nosql.comment.common;

import demo.exception.ValidationException;

@SuppressWarnings("serial")
public class InvalidReqException extends ValidationException {

	public InvalidReqException() {
		super();
	}

	public InvalidReqException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidReqException(String message) {
		super(message);
	}

	public InvalidReqException(Throwable cause) {
		super(cause);
	}

	
}
