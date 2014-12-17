package demo.util.reflections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyUtil {
	
	public static Object createProxy(Class<?> classLoaderSource, Class<?> inter, InvocationHandler h){
		return createProxy(classLoaderSource, new Class[] {inter}, h);
	}
	
	public static Object createProxy(Class<?> classLoaderSource, Class<?>[] interfaces, InvocationHandler h){
		return Proxy.newProxyInstance(classLoaderSource.getClassLoader(), interfaces, h);
	}
	
	
	public static Object invokeReal(Object real, Method m, Object[] args) throws Throwable {
		try {
			return m.invoke(real, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
		}
	}

}
