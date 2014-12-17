package demo.util;


import static demo.util.Util.*;
import demo.junit.AssertExt;
import demo.util.Util;
import demo.util.Util.DuplicateProvider;

import java.util.List;

import org.junit.Test;


public class UtilTest extends AssertExt {
	
	@Test
	public void test_randomUUID(){
		
		String str = String.valueOf(Long.MAX_VALUE);
		assertEquals(19, str.length());
		
		assertEquals(36, Util.randomUUID().length());
	}
	
	
	
	@Test
	public void test_filterByBestFromDuplicates(){
		
		{
			List<String> res = filterByBestFromDuplicates(list("a", "a", "b", "A", "c", "B"), new DuplicateProvider<String>() {
	
				@Override
				public boolean isDuplicates(String a, String b) {
					return a.equalsIgnoreCase(b);
				}
	
				@Override
				public int findBestFrom(List<String> duplicates) {
					for (int i = 0; i < duplicates.size(); i++) {
						String s = duplicates.get(i);
						if(Character.isUpperCase(s.charAt(0))){
							return i;
						}
					}
					return 0;
				}
			});
			assertEquals(list("A", "B", "c"), res);
		}
		
		
		{
			List<String> res = filterByBestFromDuplicates(list("a", "a", "b", "A", "c", "B"), new DuplicateProvider<String>() {
	
				@Override
				public boolean isDuplicates(String a, String b) {
					return a.equalsIgnoreCase(b);
				}
	
				@Override
				public int findBestFrom(List<String> duplicates) {
					return -1;
				}
			});
			assertEquals(list("c"), res);
		}
		
		{
			List<String> res = filterByBestFromDuplicates(list("a", "b", "c"), new DuplicateProvider<String>() {
	
				@Override
				public boolean isDuplicates(String a, String b) {
					return a.equalsIgnoreCase(b);
				}
	
				@Override
				public int findBestFrom(List<String> duplicates) {
					return -1;
				}
			});
			assertEquals(list("a", "b", "c"), res);
		}
		
	}

}
