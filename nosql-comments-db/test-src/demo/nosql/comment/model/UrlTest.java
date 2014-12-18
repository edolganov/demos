package demo.nosql.comment.model;

import static demo.util.Util.*;

import org.junit.Test;

import demo.junit.AssertExt;
import demo.nosql.comment.model.Url;

public class UrlTest extends AssertExt {
	
	
	@Test
	public void test_querysort(){
		
		assertEquals("ya.ru?a=1&b=2", new Url("ya.ru?b=2&a=1").val);
		assertEquals("ya.ru?a=1&a=2&b=1", new Url("ya.ru?b=1&a=2&a=1").val);
		assertEquals("ya.ru/B/C?a=1&a=2&b=1#ccc", new Url("ya.ru/B/C?b=1&a=2&a=1#ccc").val);
		
	}
	
	@Test
	public void test_getName(){
		
		assertEquals("some", new Url("some").val);
		
		assertEquals("ya.ru", new Url("http://www.ya.ru").val);
		assertEquals("ya.ru", new Url("https://www.ya.ru").val);
		assertEquals("ya.ru", new Url("www.ya.ru").val);
		assertEquals("ya.ru", new Url("ya.ru").val);
		assertEquals("ya.ru:8080", new Url("ya.ru:8080").val);
		assertEquals("ya.ru:8080?param1=1&param2=%B8", new Url("ya.ru:8080?param1=1&param2=%B8").val);
		assertEquals("127.0.0.1", new Url("127.0.0.1").val);
		
		
		assertEquals(new Url("http://www.ya.ru"), new Url(new Url("http://www.ya.ru").val));
		assertEquals(new Url("www.ya.ru"), new Url(new Url("www.ya.ru").val));
		assertEquals(new Url("ya.ru"), new Url(new Url("ya.ru").val));
		assertEquals(new Url("ya.ru:8080"), new Url(new Url("ya.ru:8080").val));
		assertEquals(new Url("ya.ru:8080?param=1&param2=%B8"), new Url(new Url("ya.ru:8080?param=1&param2=%B8").val));
		assertEquals(new Url("127.0.0.1"), new Url(new Url("127.0.0.1").val));
		
		
		assertEquals("ya.ru", new Url("  h t t p://www.ya.\n   \t    \0ru").val);
		
		
		assertEquals("chrome://chrome-urls", new Url("chrome://chrome-urls/").val);
		assertEquals("chrome://chrome-urls/a?b#c", new Url("chrome://chrome-urls/a?b#c").val);
	}
	
	
	@Test
	public void test_tokens(){
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru"), new Url("ya.ru").tokens);
		
		assertEquals(list(
				"a.b.ya.ru", 
				".ru", 
				".ya.ru",
				".b.ya.ru",
				".a.b.ya.ru"), new Url("a.b.ya.ru").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru"), new Url("http://ya.ru").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru"), new Url("http://ya.ru/").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru",  
				"ya.ru/a",
				"ya.ru/a/b",
				"ya.ru/a/b/c"), new Url("http://ya.ru/a/b/c/").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru",
				"ya.ru/a",
				"ya.ru/a/b",
				"ya.ru/a/b/c"), new Url("https://ya.ru/a/b/c/").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru", 
				"ya.ru/a",
				"ya.ru/a/b",
				"ya.ru/a/b/c", 
				"ya.ru/a/b/c?d=1&e=2"), new Url("http://ya.ru/a/b/c/?d=1&e=2").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru", 
				"ya.ru/a",
				"ya.ru/a/b",
				"ya.ru/a/b/c", 
				"ya.ru/a/b/c?d=1&e=2",
				"ya.ru/a/b/c?d=1&e=2#f"), new Url("http://ya.ru/a/b/c/?d=1&e=2#f").tokens);
		
		assertEquals(list(
				"ya.ru:90", 
				".ru:90", 
				".ya.ru:90", 
				"ya.ru:90/a",
				"ya.ru:90/a/b",
				"ya.ru:90/a/b/c", 
				"ya.ru:90/a/b/c?d=1&e=2",
				"ya.ru:90/a/b/c?d=1&e=2#f"), new Url("http://ya.ru:90/a/b/c/?d=1&e=2#f").tokens);
		
		
		assertEquals(list(
				"ftp://ya.ru:90", 
				".ru:90", 
				".ya.ru:90", 
				"ftp://ya.ru:90/a",
				"ftp://ya.ru:90/a/b",
				"ftp://ya.ru:90/a/b/c", 
				"ftp://ya.ru:90/a/b/c?d=1&e=2",
				"ftp://ya.ru:90/a/b/c?d=1&e=2#f"), new Url("ftp://ya.ru:90/a/b/c/?d=1&e=2#f").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru", 
				"ya.ru?d=1&e=2",
				"ya.ru?d=1&e=2#f"), new Url("http://ya.ru/?d=1&e=2#f").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru", 
				"ya.ru?d=1&e=2",
				"ya.ru?d=1&e=2#f"), new Url("http://ya.ru?d=1&e=2#f").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru", 
				"ya.ru#f"), new Url("http://ya.ru/#f").tokens);
		
		assertEquals(list(
				"ya.ru", 
				".ru", 
				".ya.ru", 
				"ya.ru#f"), new Url("http://ya.ru#f").tokens);
		
	}

}
