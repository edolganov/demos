package demo.socket.pool;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import static demo.util.socket.SocketUtil.*;

public abstract class SocketConn implements Closeable {
	
	public abstract InputStream getInputStream();
	
	public abstract OutputStream getOutputStream();
	
	public abstract void invalidate();
	
	public PrintWriter getWriter() throws IOException{
		return getWriterUTF8(this);
	}
	
	public BufferedReader getReader() throws IOException{
		return getReaderUTF8(this);
	}

}
