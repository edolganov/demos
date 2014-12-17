package demo.util.concurrent;

import static demo.util.Util.*;

import java.util.Collection;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;

public interface AsyncListener {
	
	public static Log listenerLog = getLog(AsyncListener.class);

	
	public static void fireAsyncEvent(Collection<AsyncListener> listeners, Future<?> future) {
		if(isEmpty(listeners)) return;
		for (AsyncListener l : listeners) fireAsyncEvent(l, future);
		
	}
	
	
	public static void fireAsyncEvent(AsyncListener l, Future<?> future) {
		if(l == null) return;
		try {
			l.onFutureEvent(future);
		}catch (Throwable t) {
			listenerLog.error("listener error", t);
		}
	}
	
	
	void onFutureEvent(Future<?> future);
}