package demo.nosql.comment.model;

public class PageId {
	
	public final Url url;
	public final int pageIndex;
	
	public PageId(String url, int pageIndex) {
		this(new Url(url), pageIndex);
	}
	
	public PageId(Url url, int pageIndex) {
		this.url = url;
		this.pageIndex = pageIndex;
	}
	
	public CommentId getCommentId(int commentIndex){
		return new CommentId(url, pageIndex, commentIndex);
	}

	@Override
	public String toString() {
		return "{url=" + url + ", pageIndex=" + pageIndex + "}";
	}
	

}
