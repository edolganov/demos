package demo.exception;

import org.apache.commons.logging.Log;

import demo.util.exception.BaseExpectedException;

@SuppressWarnings("serial")
public class ExpectedException extends BaseExpectedException {

	public ExpectedException() {
		super();
	}

	public ExpectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpectedException(String message) {
		super(message);
	}

	public ExpectedException(Throwable cause) {
		super(cause);
	}
	
	public static void logError(Log log, Throwable t, String msg){
		BaseExpectedException.logError(log, t, msg);
	}
	
	

}
