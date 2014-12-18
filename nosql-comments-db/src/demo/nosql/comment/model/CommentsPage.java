package demo.nosql.comment.model;

import demo.util.model.Pair;

/**
 * Page in pages files
 */
public class CommentsPage {
	
	/** global offset in pages files */
	public final long virtualOffset;
	
	/** cur limit */
	public volatile int limit;
	
	public CommentsPage(Pair<Long, Integer> pair) {
		this(pair.first, pair.second);
	}
	
	public CommentsPage(long virtualOffset, int limit) {
		if(virtualOffset < 0) 
			throw new IllegalArgumentException("virtualOffset < 0");
		this.virtualOffset = virtualOffset;
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "{virtualOffset="+ virtualOffset + ", limit=" + limit + "}";
	}
	
}