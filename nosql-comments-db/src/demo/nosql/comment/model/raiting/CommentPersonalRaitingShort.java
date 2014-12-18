package demo.nosql.comment.model.raiting;

import static demo.util.Util.*;

public class CommentPersonalRaitingShort {
	
	public Boolean raitingVal;
	public ClaimType claimType;
	public boolean hide;
	
	
	public void setRaitingVal(Boolean raitingVal) {
		this.raitingVal = raitingVal;
	}
	public void setClaimCode(Integer code) {
		this.claimType = tryGetEnumByCode(code, ClaimType.class, null);
	}
	public void setClaimType(ClaimType claimType) {
		this.claimType = claimType;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	
	

}
