package demo.nosql.comment.model.ban;

import java.util.HashSet;
import java.util.List;

import demo.util.json.GsonUtil;
import static demo.util.StringUtil.*;

import static demo.util.Util.*;

public class BannedCommentsPage {
	
	public long id;
	public HashSet<BannedComment> bannedIndexes = new HashSet<>();
	

	public boolean hasIndex(int commentIndex){
		return bannedIndexes.contains(new BannedComment(commentIndex, null));
	}
	
	public void appendIndex(int commentIndex, CommentBanReason reason) {
		bannedIndexes.add(new BannedComment(commentIndex, reason));
	}
	
	public void removeIndex(int commentIndex){
		bannedIndexes.remove(new BannedComment(commentIndex, null));
	}
	
	public String getCommentsJson() {
		return collectionToStr(bannedIndexes, '[', ']');
	}
	
	public static String getSingleCommentJson(int commentIndex, CommentBanReason reason){
		return "["+new BannedComment(commentIndex, reason)+"]";
	}
	
	
	
	public void setId(long id) {
		this.id = id;
	}
	public void setBannedIndexes(String jsonArray) {
		
		if(hasText(jsonArray)){
			List<String> list = GsonUtil.getList(jsonArray, String.class);
			for (String json : list) {
				bannedIndexes.add(new BannedComment(json));
			}
		}
		
	}

}
