package demo.nosql.comment.model.raiting;

import demo.nosql.comment.model.CommentId;

public class CommentTotalRaiting extends CommentTotalRaitingShort {
	
	public String url;
	public int pageIndex;
	
	
	public CommentTotalRaiting() {}
	
	public CommentTotalRaiting(CommentId commentId){
		this(commentId.url.val, commentId.pageIndex, commentId.commentIndex);
	}
	
	public CommentTotalRaiting(String url, int pageIndex, int commentIndex) {
		this.url = url;
		this.pageIndex = pageIndex;
		this.commentIndex = commentIndex;
	}
	
	public CommentId getCommentId(){
		return new CommentId(url, pageIndex, commentIndex);
	}


	public void setUrl(String url) {
		this.url = url;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
}
