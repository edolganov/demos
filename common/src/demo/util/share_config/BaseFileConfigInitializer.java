package demo.util.share_config;

import java.io.File;

import org.apache.commons.logging.Log;

import static demo.util.FileUtil.*;
import static demo.util.Util.*;
import static demo.util.exception.BaseExpectedException.*;



public abstract class BaseFileConfigInitializer {
	
	protected Log log = getLog(getClass());
	protected String filePath;

	public BaseFileConfigInitializer(String filePath) {
		this.filePath = filePath;
	}

	public void reinitFromFile() {
		try {
			
			if(filePath == null) return;
			
			File file = new File(filePath);
			if( ! file.exists() || ! file.isFile()){
				log.info("no file by path: "+filePath);
				return;
			}
			
			long lastModified = file.lastModified();
			String content = readFileUTF8(file);
			setContent(lastModified, content);
			log.info("reinited file: "+filePath);
			
		}catch (Throwable t) {
			logError(log, t, "can't reinitFromFile");
		}
	}
	
	protected abstract void setContent(long lastModified, String content) throws Exception;

}
