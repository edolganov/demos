package demo.nosql.comment.model;

import java.io.Serializable;

import static demo.model.GlobalConst.*;
import static demo.util.ArrayUtil.*;
import static demo.util.Util.*;


@SuppressWarnings("serial")
public class PagesBlock implements Serializable {
	
	public final int pageBlockIndex;
	public final long offset;
	
	private volatile CommentsPage[] pages;
	
	public PagesBlock(int pageBlockIndex, long offset, CommentsPage... pages) {
		
		if(isEmpty(pages)) throw new IllegalArgumentException("pages is empty");
		
		this.pageBlockIndex = pageBlockIndex;
		this.offset = offset;
		this.pages = pages;
	}
	
	public void addPage(CommentsPage page) {
		if(pages == null) {
			pages = new CommentsPage[]{page};
			return;
		}
		
		CommentsPage[] newPages = new CommentsPage[pages.length+1];
		copyFromSmallToBig(pages, newPages, 0);
		newPages[newPages.length-1] = page;
		pages = newPages;
		
	}
	
	
	public int pagesSize(){
		return pages == null? 0 : pages.length;
	}
	
	public CommentsPage getPage(int index){
		return pages[index];
	}

	public CommentsPage findPage(int pageIndex) {
		
		CommentsPage[] curPages = pages;
		if(isEmpty(curPages)) return null;
		
		int localIndex = pageIndex - (pageBlockIndex * MAX_PAGES_COUNT);
		if(localIndex < 0 || localIndex > curPages.length-1) return null;
		return curPages[localIndex];
	}
	
	
}