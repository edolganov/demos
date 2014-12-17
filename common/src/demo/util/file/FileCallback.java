package demo.util.file;

import java.io.File;

public interface FileCallback {
	
	void onOpenStream(File file) throws Exception;
	
}