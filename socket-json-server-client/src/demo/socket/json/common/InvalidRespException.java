package demo.socket.json.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class InvalidRespException extends IOException {

	public InvalidRespException() {
		super();
	}

	public InvalidRespException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRespException(String message) {
		super(message);
	}

	public InvalidRespException(Throwable cause) {
		super(cause);
	}

	
}
