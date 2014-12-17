package demo.util;

import static demo.util.ReflectionsUtil.*;
import demo.junit.AssertExt;

import org.junit.Test;


public class ReflectionsUtilTest extends AssertExt {
	
	@Test
	public void test_getFirstActualArgType(){
		
		assertEquals(String.class, getFirstActualArgType(RealB.class));
		assertEquals(String.class, getFirstActualArgType(RealD.class));
	}
	
	public static class BaseA<I> {}
	
	//one level
	public static class RealB extends BaseA<String> {}
	
	public static class BaseC<I> extends BaseA<I> {}
	
	//two levels
	public static class RealD extends BaseC<String> {}

}
