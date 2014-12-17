package json;

import com.google.gson.Gson;


import demo.junit.AssertExt;
import demo.util.json.JsonUtil;

import org.junit.Test;


public class GsonAndJsonTest extends AssertExt {
	
	Gson gson = new Gson();
	
	@Test
	public void test_string_escape(){
		
		assertEqualsEscape("\\n\\n\\n", "\n\n\n");
		assertEqualsEscape("\\r\\r\\r", "\r\r\r");
		assertEqualsEscape("\\\"\\\"\\\"", "\"\"\"");
		assertEqualsEscape("\\u0000", "\0");
		assertEqualsEscape("\\u003cb/\\u003e", "<b/>");
		assertEqualsEscape("абв", "абв");
		assertEqualsEscape("عدد", "عدد");
		assertEqualsEscape("///", "///");
	}
	
	private void assertEqualsEscape(String result, String val){
		assertEquals("\""+result+"\"", gson.toJson(val));
		assertEquals(result, JsonUtil.escapeStr(val, true));
	}

}
