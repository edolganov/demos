package demo.nosql.comment.model.raiting;

import demo.util.model.JsonConvertible;

public class CommentPersonalRaiting extends CommentPersonalRaitingShort implements JsonConvertible {
	
	public int commentIndex;

	public void setCommentIndex(int commentIndex) {
		this.commentIndex = commentIndex;
	}

	@Override
	public String toJson() {
		return "{\"index\":"+commentIndex
				+(raitingVal != null ? ", \"raiting\":"+raitingVal : "")
				+(hide? ", \"hide\":1" : "")
				+(claimType != null ? ", \"claim\":"+claimType.code : "")
				+"}";
	}

}
