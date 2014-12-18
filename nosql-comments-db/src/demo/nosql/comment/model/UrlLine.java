package demo.nosql.comment.model;


public class UrlLine {
	
	public final long offset;
	
	/**
	 * Index of pages block in the global context of all pages.
	 */
	public final int pageBlockIndex;
	public final String url;
	public final CommentsPage[] pages;
	
	public UrlLine(long offset, int globalOrder, String url, CommentsPage[] pages) {
		this.offset = offset;
		this.pageBlockIndex = globalOrder;
		this.url = url;
		this.pages = pages;
	}
	
	public PagesBlock toBlock(){
		return new PagesBlock(pageBlockIndex, offset, pages);
	}
	
}