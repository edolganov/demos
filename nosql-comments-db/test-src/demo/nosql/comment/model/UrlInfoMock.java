package demo.nosql.comment.model;



public class UrlInfoMock {
	
	public final long offset = 0;
	public final int globalOrder = 0;
	public final PageInfoMock[] pages;
	
	public UrlInfoMock(int size) {
		pages = new PageInfoMock[size];
		for (int i = 0; i < size; i++) {
			pages[i] = new PageInfoMock();
		}
	}
}