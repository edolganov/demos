package demo.util.io;

import java.io.ByteArrayOutputStream;

public class OpenByteArrayOutputStream extends ByteArrayOutputStream {

	public OpenByteArrayOutputStream() {
		super();
	}

	public OpenByteArrayOutputStream(int size) {
		super(size);
	}
	
	public byte[] getBuf(){
		return buf;
	}
	
	

}
