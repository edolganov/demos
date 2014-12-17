package demo.socket.json.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class UnexpectedServerException extends IOException {

	public UnexpectedServerException() {
		super();
	}

	public UnexpectedServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedServerException(String message) {
		super(message);
	}

	public UnexpectedServerException(Throwable cause) {
		super(cause);
	}

	
}
