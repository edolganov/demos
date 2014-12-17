package demo.util.file;

import java.io.File;

public class SimpleFileStorage extends AbstractFileStorage {

	@Override
	public File getFile(String path) throws Exception {
		return new File(path);
	}

}
