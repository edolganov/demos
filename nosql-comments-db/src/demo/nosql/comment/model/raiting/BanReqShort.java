package demo.nosql.comment.model.raiting;

import java.util.HashSet;
import java.util.List;

import static demo.util.StringUtil.*;

import static demo.util.Util.*;

public class BanReqShort {
	
	public int claimTotal;
	public String claimCodes;
	
	public void setClaimTotal(int claimTotal) {
		this.claimTotal = claimTotal;
	}
	
	public void setClaimCodes(String claimCodes) {
		this.claimCodes = claimCodes;
	}
	
	public static String getNewClaimCodes(String old, ClaimType newType){
		if(newType ==  null) return old;
		if(isEmpty(old)) return String.valueOf(newType.code);
		
		int maxCount = 5;
		List<String> vals = strToList(old);
		HashSet<ClaimType> types = new HashSet<>();
		//parse old
		for(String val : vals){
			ClaimType type = tryGetEnumByCode(tryParseInt(val, -1), ClaimType.class, null);
			if(type != null && types.size() <= maxCount) types.add(type);
		}
		//add new
		if(types.size() <= maxCount) types.add(newType);
		
		return collectionToStr(types);
	}
	

}
