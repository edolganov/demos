package demo.nosql.comment;

import static demo.util.Util.*;

import java.io.IOException;
import java.util.List;

import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentId;
import demo.util.io.LimitInputStream;

public class CommentDbCommon {
	
	public static Comment getCommentFromStream(LimitInputStream is, CommentId commentId) throws IOException {
		List<Comment> list = Comment.fromJsonStream(is);
		if(isEmpty(list)) return null;
		
		int index = commentId.commentIndex;
		return index < list.size()? list.get(index) : null;
	}

}
