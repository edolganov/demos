package demo.exception.io;

import java.io.File;

import demo.exception.ExpectedException;


@SuppressWarnings("serial")
public class BrokenFileException extends ExpectedException {
	
	public BrokenFileException(File file, long offset) {
		super("file: "+file+", offset: "+offset);
	}
	

	public BrokenFileException() {
		super();
	}

	public BrokenFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public BrokenFileException(String message) {
		super(message);
	}

	public BrokenFileException(Throwable cause) {
		super(cause);
	}
	

}
