package demo.nosql.comment.model.raiting;

import demo.util.model.JsonConvertible;


public class CommentTotalRaitingShort implements JsonConvertible {
	
	public int commentIndex;
	public int raitingTotal;
	public int claimTotal;
	
	public void setRaitingTotal(int raitingTotal) {
		this.raitingTotal = raitingTotal;
	}
	
	public void setClaimTotal(int claimTotal) {
		this.claimTotal = claimTotal;
	}
	
	public void setCommentIndex(int commentIndex) {
		this.commentIndex = commentIndex;
	}

	@Override
	public String toJson() {
		return "{\"index\":"+commentIndex+", \"raiting\":"+raitingTotal+", \"claim\":"+claimTotal+"}";
	}
	
	

	
	

}
