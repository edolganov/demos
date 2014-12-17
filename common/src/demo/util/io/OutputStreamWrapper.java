package demo.util.io;

import java.io.IOException;
import java.io.OutputStream;

import static demo.util.StringUtil.*;

/**
 * for write(String str) method
 */
public class OutputStreamWrapper extends OutputStream {
	
	public final OutputStream os;
	
	public OutputStreamWrapper(OutputStream os) {
		this.os = os;
	}
	
	
	
	public void write(String str) throws IOException {
		os.write(getBytesUTF8(str));
	}
	
	public void write(Object b) throws IOException {
		os.write(getBytesUTF8(String.valueOf(b)));
	}
	
	

	@Override
	public void write(byte b[]) throws IOException {
		os.write(b);
	}

	@Override
	public void write(int b) throws IOException {
		os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	@Override
	public void close() throws IOException {
		os.close();
	}
	
	

}
