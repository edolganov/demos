package demo.nosql.comment.model;

import static demo.util.json.GsonUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import demo.junit.AssertExt;
import demo.nosql.comment.model.Comment;
import demo.util.StreamUtil;
import demo.util.json.GsonUtil;

public class CommentTestCommon {
	
	public static void checkStreamToCommentsContent(InputStream in, String... contents) throws IOException{
		String str = in == null? null : StreamUtil.streamToStr(in);
		AssertExt.assertNotNull(str);
		str = "["+str+"]";
		
		List<Comment> list = GsonUtil.getList(str, Comment.class, defaultGson);
		AssertExt.assertEquals(contents.length, list.size());
		for (int i = 0; i < contents.length; i++) {
			String expected = contents[i];
			String actual = list.get(i).getContent();
			AssertExt.assertEquals(expected, actual);
		}
		
	}

}
