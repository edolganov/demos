package demo.nosql.comment.model.ban;

import demo.util.model.JsonConvertible;

import static demo.util.Util.*;

public class BannedComment implements JsonConvertible {
	
	public final int index;
	public final CommentBanReason reason;
	
	public BannedComment(String json) {
		if( ! hasText(json)) json = "";
		json = json.replace("\"", "");
		int sepIndex = json.indexOf('-');
		if(sepIndex > 0){
			index = tryParseInt(json.substring(0, sepIndex), 0);
			int enumCode = tryParseInt(json.substring(sepIndex+1), 0);
			reason = tryGetEnumByCode(enumCode, CommentBanReason.class, null);
		} else {
			index = tryParseInt(json, 0);
			reason = null;
		}
	}
	
	public BannedComment(int index, CommentBanReason reason) {
		this.index = index;
		this.reason = reason;
	}
	
	@Override
	public String toJson() {
		return "\"" + index + (reason != null? "-" + reason.getCode() : "") + "\"";
	}
	
	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BannedComment other = (BannedComment) obj;
		if (index != other.index)
			return false;
		return true;
	}


}