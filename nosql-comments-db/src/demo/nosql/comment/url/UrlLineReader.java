package demo.nosql.comment.url;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import demo.exception.io.BrokenFileException;
import demo.nosql.comment.model.UrlLine;
import static demo.nosql.comment.url.UrlLineConverter.*;

public class UrlLineReader implements Closeable {
	
	
	private static final int OFFSET_ERROR_STATE = -2;
	private static final int OFFSET_EMPTY_STATE = -3;
	
	private final File file;
	private final BufferedInputStream is;
	private final byte[] lineBuffer;
	private long curOffset;
	
	public UrlLineReader(File file) throws FileNotFoundException {
		this.file = file;
		
		if(file.length() == 0) {
			curOffset = OFFSET_EMPTY_STATE;
			is = null;
			lineBuffer = null;
		} else {
			is = new BufferedInputStream(new FileInputStream(file));
			lineBuffer = new byte[LINE_BYTES_COUNT];
		}
	}

	public UrlLine next() throws IOException {
		
		if(curOffset == OFFSET_EMPTY_STATE) {
			return null;
		}
		if(curOffset == OFFSET_ERROR_STATE){
			throw new IllegalStateException("can't read from file "+file+": need close");
		}
		
		long offset = curOffset;
		int readed = is.read(lineBuffer, 0 , lineBuffer.length);
		if(readed == -1) return null;
		if(readed < lineBuffer.length) {
			curOffset = OFFSET_ERROR_STATE;
			throw new BrokenFileException(file, offset);
		}
		
		curOffset += readed;
		
		UrlLine line = bytesToUrlLine(offset, lineBuffer);
		if(line == null) throw new BrokenFileException(file, offset);
		return line;
	}
	
	@Override
	public void close() throws IOException{
		if(is != null) is.close();
	}

}
