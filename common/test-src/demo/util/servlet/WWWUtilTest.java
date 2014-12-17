package demo.util.servlet;


import demo.junit.AssertExt;

import org.junit.Test;




import static demo.util.Util.*;
import static demo.util.servlet.WWWUtil.*;


public class WWWUtilTest extends AssertExt {
	
	@Test
	public void test_WWW(){
		
		assertEquals(list("site.com/123", "www.site.com/123"), getWWW_and_short_fromUrl("site.com/123"));
		assertEquals(list("http://site.com", "http://www.site.com"), getWWW_and_short_fromUrl("http://site.com"));
		assertEquals(list("http://site.com", "http://www.site.com"), getWWW_and_short_fromUrl("http://www.site.com"));
		assertEquals(list("https://site.com", "https://www.site.com"), getWWW_and_short_fromUrl("https://site.com"));
		assertEquals(list("https://site.com", "https://www.site.com"), getWWW_and_short_fromUrl("https://www.site.com"));
		
		assertEquals(false, hasWWWPrefix("site.com/123"));
		assertEquals(false, hasWWWPrefix("http://site.com"));
		assertEquals(true, hasWWWPrefix("http://www.site.com"));
		assertEquals(false, hasWWWPrefix("https://site.com"));
		assertEquals(true, hasWWWPrefix("https://www.site.com"));
		
		assertEquals("www.site.com/123", getUrlWithWWW("site.com/123"));
		assertEquals("http://www.site.com", getUrlWithWWW("http://site.com"));
		assertEquals("http://www.site.com", getUrlWithWWW("http://www.site.com"));
		assertEquals("https://www.site.com", getUrlWithWWW("https://site.com"));
		assertEquals("https://www.site.com", getUrlWithWWW("https://www.site.com"));
		
		assertEquals("site.com/123", getUrlWithoutWWW("site.com/123"));
		assertEquals("http://site.com", getUrlWithoutWWW("http://site.com"));
		assertEquals("http://site.com", getUrlWithoutWWW("http://www.site.com"));
		assertEquals("https://site.com", getUrlWithoutWWW("https://site.com"));
		assertEquals("https://site.com", getUrlWithoutWWW("https://www.site.com"));
		
		
	}
}
