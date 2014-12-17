package demo.util;

import static demo.util.ArrayUtil.*;
import demo.junit.AssertExt;

import java.nio.ByteBuffer;




import org.junit.Test;


public class ArrayUtilTest extends AssertExt {
	
	@Test
	public void test_convert(){
		
		assertEquals(0, getLong(getLongBytes(0)));
		assertEquals(256, getLong(getLongBytes(256)));
		assertEquals(-256, getLong(getLongBytes(-256)));
		assertEquals(Long.MAX_VALUE, getLong(getLongBytes(Long.MAX_VALUE)));
		assertEquals(Long.MIN_VALUE, getLong(getLongBytes(Long.MIN_VALUE)));
		
	}
	
	@Test
	public void test_check_with_ByteBuffer(){
		
		byte[] bytes = new byte[]{0,0,0,0,-128,0,59,48};
		
		ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    long val = buffer.getLong();
	    long val2 = getLong(bytes, 0);
	    assertEquals(val, val2);
		
	}

}
