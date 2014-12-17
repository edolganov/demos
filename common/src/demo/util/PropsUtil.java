package demo.util;

import static demo.util.Util.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropsUtil {
	
	public static Properties loadPropsFromFile(File file) throws IOException {
		FileReader reader = new FileReader(file);
		Properties props = new Properties();
		try {
			props.load(reader);
		} finally {
			reader.close();
		}
		return props;
	}
	
	
	public static String getNotEmptyProperty(Properties props, String key){
		String out = props.getProperty(key);
		if(isEmpty(out)) throw new IllegalStateException("empty property by key "+key);
		return out;
	}
	
	public static Integer getIntProperty(Properties props, String key, Integer defaultVal){
		String str = props.getProperty(key);
		return tryParseInt(str, defaultVal);
	}

}
