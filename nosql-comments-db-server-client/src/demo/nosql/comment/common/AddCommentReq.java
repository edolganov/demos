package demo.nosql.comment.common;

import demo.model.BaseBean;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.Url;

public class AddCommentReq extends BaseBean {
	
	public String url;
	public long userId = -1;
	public String content;
	
	
	
	public AddCommentReq() {
		super();
	}
	
	public AddCommentReq(Url url, Comment c) {
		this(url.val, c.author, c.getContent());
	}
	

	public AddCommentReq(String url, long userId, String content) {
		super();
		this.url = url;
		this.userId = userId;
		this.content = content;
	}





	@Override
	protected void checkState(Errors errors) {
		checkForText(url, "url", errors);
		checkForText(content, "content", errors);
		checkForValid(userId > -1, "userId", errors);
	}

}
