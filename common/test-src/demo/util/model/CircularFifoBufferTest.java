package demo.util.model;


import static demo.util.Util.*;
import demo.junit.AssertExt;
import demo.util.Util;
import demo.util.model.CircularFifoBuffer;

import java.util.Iterator;

import org.junit.Test;

public class CircularFifoBufferTest extends AssertExt {
	
	
	@Test
	public void test_buffer(){
		
		CircularFifoBuffer<Integer> buffer = new CircularFifoBuffer<>(3);
		
		buffer.add(1);
		assertEquals(Util.list(1), toList(buffer));
		
		buffer.add(2);
		assertEquals(Util.list(1, 2), toList(buffer));
		
		buffer.add(3);
		assertEquals(Util.list(1, 2, 3), toList(buffer));
		
		buffer.add(4);
		assertEquals(Util.list(2, 3, 4), toList(buffer));
		
		Iterator<Integer> it = buffer.iterator();
		assertEquals(new Integer(2), it.next());
		assertEquals(new Integer(3), it.next());
		assertEquals(new Integer(4), it.next());
		assertEquals(false, it.hasNext());
		
		
		
	}

}
