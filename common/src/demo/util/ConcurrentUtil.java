package demo.util;

import static demo.util.Util.*;

import java.util.Collection;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;

public class ConcurrentUtil {
	
	private static Log log = getLog(ConcurrentUtil.class);
	
	public static void getAndClearAllFutures(Collection<Future<?>> futures){
		getAndClearAllFutures(futures, true);
	}
	
	public static void getAndClearAllFutures(Collection<Future<?>> futures, boolean logOnError){
		if(isEmpty(futures)) return;
		for (Future<?> future : futures) {
			try {
				future.get();
			}catch(Exception e){
				if(logOnError) log.error("can't get future", e);
			}
		}
		futures.clear();
	}

}
