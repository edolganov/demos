package demo.util;


import demo.junit.AssertExt;

import org.junit.Test;

import static demo.util.MathUtil.*;

public class MathUtilTest extends AssertExt {
	
	@Test
	public void test_round(){
		
		assertEquals(0, roundUp(0, 15));
		assertEquals(15, roundUp(1, 15));
		assertEquals(15, roundUp(14, 15));
		assertEquals(15, roundUp(15, 15));
		assertEquals(30, roundUp(16, 15));
		assertEquals(-30, roundUp(-16, 15));
		
		
		assertEquals(0, roundDown(0, 15));
		assertEquals(0, roundDown(1, 15));
		assertEquals(0, roundDown(14, 15));
		assertEquals(15, roundDown(15, 15));
		assertEquals(15, roundDown(16, 15));
		assertEquals(-15, roundDown(-16, 15));
	}
	


}
