package demo.util.web.rest;

import static demo.util.Util.*;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

public class RestUtil {
	
	
	public static List<String> getRestElems(HttpServletRequest req){
		return getRestElems(req.getPathInfo());
	}
	
	public static List<String> getRestElems(String path){
		if(isEmpty(path) || "/".equals(path)){
			return emptyList();
		}
		
		ArrayList<String> elems = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(path, "/");
		while(st.hasMoreTokens()){
			String nextToken = st.nextToken();
			if(isEmpty(nextToken)) continue;
			elems.add(nextToken);
		}
		return elems;
		
	}

}
