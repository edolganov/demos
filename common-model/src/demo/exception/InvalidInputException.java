package demo.exception;

@SuppressWarnings("serial")
public class InvalidInputException extends ValidationException {

	public InvalidInputException() {
		super();
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
	}
	

}
