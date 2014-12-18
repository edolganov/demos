package demo.nosql.comment.model.raiting;



public class BanReq extends BanReqShort {
	
	public String url;
	public int pageIndex;
	public int commentIndex;
	public long userId;
	public String comment;
	
	public void setCommentIndex(int commentIndex) {
		this.commentIndex = commentIndex;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	

}
