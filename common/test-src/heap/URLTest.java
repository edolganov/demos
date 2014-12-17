package heap;

import demo.junit.AssertExt;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class URLTest extends AssertExt {
	
	@Test
	public void test_host() throws Exception{
		
		try {
			new URL("ya.ru");
			fail_exception_expected();
		}catch(MalformedURLException e){
			//ok
		}
		
		assertEquals("ya.ru", new URL("http://ya.ru").getHost());
		assertEquals("1.ru", new URL("http://1.ru").getHost());
		assertEquals("1.ru", new URL("https://1.ru").getHost());
		
	}

}
