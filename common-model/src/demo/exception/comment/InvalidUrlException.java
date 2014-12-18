package demo.exception.comment;

import demo.exception.ValidationException;

@SuppressWarnings("serial")
public class InvalidUrlException extends ValidationException {

	public InvalidUrlException() {
		super();
	}

	public InvalidUrlException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidUrlException(String message) {
		super(message);
	}

	public InvalidUrlException(Throwable cause) {
		super(cause);
	}

	

}
