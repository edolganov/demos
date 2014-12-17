package demo.util;

import static demo.util.MapUtil.*;
import static demo.util.Util.*;
import demo.junit.AssertExt;

import org.junit.Test;

public class MapUtilTest extends AssertExt {
	
	@Test
	public void test_updated_keys(){
		
		// . . 
		assertEquals(set(), getUpdatedKeys(map(1, 1, 2, 2), map(1, 1, 2, 2)));
		// . .+
		assertEquals(set(2), getUpdatedKeys(map(1, 1), map(1, 1, 2, 2)));
		// .-. 
		assertEquals(set(2), getUpdatedKeys(map(1, 1, 2, 2), map(1, 1)));
		// .-.+
		assertEquals(set(2, 3), getUpdatedKeys(map(1, 1, 2, 2), map(1, 1, 3, 3)));
		//~. .
		assertEquals(set(2), getUpdatedKeys(map(1, 1, 2, 2), map(1, 1, 2, 0)));
		//~. .+
		assertEquals(set(2, 3), getUpdatedKeys(map(1, 1, 2, 2), map(1, 1, 2, 0, 3, 3)));
		//~.-.
		assertEquals(set(1, 2), getUpdatedKeys(map(1, 1, 2, 2), map(2, 0)));
		//~.-.+
		assertEquals(set(1, 2, 3), getUpdatedKeys(map(1, 1, 2, 2), map(1, 0, 3, 3)));


	}

}
