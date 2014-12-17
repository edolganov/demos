package demo.util.log;

import static demo.util.Util.*;
import demo.util.concurrent.ExecutorsUtil;
import demo.util.reflections.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsyncLogFactory {
	
	public static final Set<String> ASYNC_METHODS = set("debug", "error", "fatal", "info", "trace", "warn");
	
	public static class AsyncHandler implements InvocationHandler {
		
		final ExecutorService executor;
		final Log real;
		
		public AsyncHandler(ExecutorService executor, Log real) {
			this.executor = executor;
			this.real = real;
		}

		@Override
		public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
			
			String name = m.getName();
			if( ! ASYNC_METHODS.contains(name)){
				return ProxyUtil.invokeReal(real, m, args);
			}
			
			//async
			executor.submit(()->{
				try {
					ProxyUtil.invokeReal(real, m, args);
				}catch(Throwable t){
					t.printStackTrace();
				}
			});
			return null;
			

		}
		
	}
	
	
	private static final ExecutorService executor = ExecutorsUtil.newSingleThreadExecutor("async-logs-thread");
	static {
		LogFactory.getLog(AsyncLogFactory.class).info("\n***\nASYNC LOG FACTORY INITED\n***");
	}
	
	public static Log createAsyncLog(Log log){
		AsyncHandler h = new AsyncHandler(executor, log);
		return (Log) ProxyUtil.createProxy(AsyncLogFactory.class, Log.class, h);
	}

}
