package demo.nosql.comment.model.ban;

import demo.util.model.JsonConvertible;
import static demo.util.json.GsonUtil.*;



public class BannedCommentInfo implements JsonConvertible {
	
	public int commentIndex;
	public String info;
	public long userId;
	
	public void setCommentIndex(int commentIndex) {
		this.commentIndex = commentIndex;
	}
	public void setInfo(String info) {
		this.info = info != null? info : "";
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	@Override
	public String toJson() {
		return "{\"commentIndex\":"+commentIndex
				+", \"userId\":"+userId
				+", \"info\":"+defaultGson.toJson(info)
				+"}";
	}
	
	

}
