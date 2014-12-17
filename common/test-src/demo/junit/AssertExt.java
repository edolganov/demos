package demo.junit;


import static demo.util.ExceptionUtil.*;
import demo.util.ExceptionUtil;
import demo.util.StreamUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Assert;

public class AssertExt extends Assert {
	
	public static void fail_TODO(){
		fail("TODO");
	}
	
	public static void fail_exception_expected(){
		fail("Exception expected");
	}
	
	public static void assertExceptionWithText(Throwable t, String... texts){
		assertTrue(t.toString(), containsAnyTextInMessage(t, texts));
	}
	
	public static void assertFileExists(String path){
		assertTrue("assertFileExists:"+path, new File(path).exists());
	}
	
	public static void assertFileNotExists(String path){
		assertTrue("assertFileNotExists:"+path, ! new File(path).exists());
	}
	
	public static void assertEquals(String expected, String actual){
		assertEquals((Object)expected, (Object)actual);
	}
	
	public static void assertEquals(String expected, InputStream in){
		try {
			String actual = in == null? null : StreamUtil.streamToStr(in);
			assertEquals(expected, actual);
		}catch (Exception e) {
			throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
		}
	}
	
	public static void assertEquals(byte[] expected, byte[] actual){
		assertTrue("arrays are not equals", Arrays.equals(expected, actual));
	}
	

}
