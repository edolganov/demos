package demo.util.log;

import static demo.util.Util.*;

import org.apache.commons.logging.Log;

public class LogUtil {
	
	private static final String USE_ASYNC_LOG = "useAsyncLog";

	public static Log getAsyncLogIfNeed(Log log){
		
		if( ! tryParseBool(System.getProperty(USE_ASYNC_LOG), false)){
			return log;
		}

		return AsyncLogFactory.createAsyncLog(log);
	}

}
