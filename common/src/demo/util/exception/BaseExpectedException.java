package demo.util.exception;


import demo.util.annotation.NoLog;
import demo.util.socket.SocketUtil;

import java.io.IOException;

import org.apache.commons.logging.Log;


@SuppressWarnings("serial")
public abstract class BaseExpectedException extends RuntimeException {

	public BaseExpectedException() {
		super();
	}

	public BaseExpectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseExpectedException(String message) {
		super(message);
	}

	public BaseExpectedException(Throwable cause) {
		super(cause);
	}
	
	
	public static void logError(Log log, Throwable t, String msg){
		
		NoLog noLog = t.getClass().getAnnotation(NoLog.class);
		if(noLog != null){
			return;
		}
		
		if(t instanceof BaseExpectedException) {
			log.error(msg+": "+t);
			return;
		}
		
		IOException socketEx = SocketUtil.findSocketException(t);
		if(socketEx != null){
			log.error(msg+": "+new ExpectedSocketException(socketEx, t));
			return;
		}
		
		//not ExpectedException
		log.error(msg, t);
	}
	
	

}
