package demo.nosql.comment.model;

public class CommentId {
	
	public final Url url;
	public final int pageIndex;
	public final int commentIndex;
	
	public CommentId(String url, int pageIndex, int commentIndex) {
		this(new Url(url), pageIndex, commentIndex);
	}
	
	public CommentId(Url url, int pageIndex, int commentIndex) {
		this.url = url;
		this.pageIndex = pageIndex;
		this.commentIndex = commentIndex;
	}
	
	public PageId getPageId(){
		return new PageId(url, pageIndex);
	}
	
	public CommentId next(){
		return create(commentIndex+1);
	}
	
	public CommentId create(int commentIndex){
		return new CommentId(url, pageIndex, commentIndex);
	}

	@Override
	public String toString() {
		return "{url=" + url + ", pageIndex=" + pageIndex
				+ ", commentIndex=" + commentIndex + "}";
	}
	

}
