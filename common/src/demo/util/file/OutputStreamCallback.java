package demo.util.file;

import java.io.FileOutputStream;

public interface OutputStreamCallback {
	
	void onOpenStream(FileOutputStream os) throws Exception;
	
}