package demo.util.file;

import java.io.FileInputStream;

public interface InputStreamCallback {
	
	void onOpenStream(FileInputStream is) throws Exception;
	
}