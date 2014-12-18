package demo.nosql.comment.model;

import static demo.util.ArrayUtil.*;

import static demo.util.Util.*;

public class UrlState {
	
	private volatile PagesBlock[] blocks;
	
	public UrlState() {
		super();
	}
	
	public UrlState(PagesBlock block) {
		addBlock(block);
	}


	public int blocksSize(){
		return blocks == null? 0 : blocks.length;
	}
	
	public PagesBlock getBlock(int index){
		return blocks[index];
	}
	
	
	public void addBlock(PagesBlock block) {
		if(blocks == null) {
			blocks = new PagesBlock[]{block};
			return;
		}
		
		PagesBlock[] newBlocks = new PagesBlock[blocks.length+1];
		copyFromSmallToBig(blocks, newBlocks, 0);
		newBlocks[newBlocks.length-1] = block;
		blocks = newBlocks;
		
	}

	public CommentsPage findPage(int pageIndex) {
		
		PagesBlock[] curBlocks = blocks;
		if(isEmpty(curBlocks)) return null;
		
		CommentsPage page = null;
		for (int i = 0; i < curBlocks.length; i++) {
			page = curBlocks[i].findPage(pageIndex);
			if(page != null) break;
		}
		return page;
	}

}
