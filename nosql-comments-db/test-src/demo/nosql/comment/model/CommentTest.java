package demo.nosql.comment.model;

import static demo.nosql.comment.model.Comment.*;
import static demo.util.StringUtil.*;
import static demo.util.json.JsonUtil.*;

import org.junit.Test;

import demo.junit.AssertExt;
import demo.nosql.comment.model.Comment;

public class CommentTest extends AssertExt {
	
	@Test
	public void test_content_escaped_and_max_length(){
		
		assertEquals_escaped(createStr('z', CONTENT_MAX_SIZE), createStr('z', CONTENT_MAX_SIZE));
		assertEquals_escaped(createStr('я', CONTENT_MAX_SIZE), createStr('я', CONTENT_MAX_SIZE));
		assertEquals_escaped(createStr('я', CONTENT_MAX_SIZE), createStr('я', CONTENT_MAX_SIZE+1));
		
		assertEquals_escaped("\\u003cb/\\u003e", "<b/>");
		assertEquals_escaped("\\\"", "\"");
		
		assertEquals_escaped("\\u003cb/\\u003e"+createStr('я', CONTENT_MAX_SIZE-4-10), "<b/>"+createStr('я', CONTENT_MAX_SIZE-4));
		assertEquals_escaped("\\u003cb/\\u003e"+createStr('я', CONTENT_MAX_SIZE-4-10), "<b/>"+createStr('я', CONTENT_MAX_SIZE-3));
		
		assertEquals_escaped(createStr('я', CONTENT_MAX_SIZE-4), createStr('я', CONTENT_MAX_SIZE-4)+"<b/>");
		
		assertEquals_escaped(createStr('я', CONTENT_MAX_SIZE/2-2)+"\\u003cb/\\u003e"+createStr('я', CONTENT_MAX_SIZE/2-2-10), 
				createStr('я', CONTENT_MAX_SIZE/2-2)+"<b/>"+createStr('я', CONTENT_MAX_SIZE/2-2));
	}
	
	private void assertEquals_escaped(String expacted, String initStr){
		Comment c = new Comment(initStr);
		String content = c.getContent();
		String escaped = escapeStr(content, true);
		assertTrue(escaped.length()+" > "+CONTENT_MAX_SIZE, escaped.length() <= CONTENT_MAX_SIZE);
		assertEquals(expacted, escaped);
	}

}
