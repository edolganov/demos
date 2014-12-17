package demo.util.json;

import static demo.util.CommonConst.*;
import static demo.util.Util.*;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import demo.util.Util;


public class GsonUtil {
	
	public static final Gson defaultGson = gsonWithFullDateFormat();
	public static final Gson defaultGsonPrettyPrinting = gsonWithFullDateFormat(true);
	
	public static <T> List<T> getList(String content, Class<T> type){
		return getList(content, type, new Gson());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(String content, Class<T> type, Gson gson){
		
		if(Util.isEmpty(content)){
			return Collections.emptyList();
		}
		
		Object arrayObject = Array.newInstance(type,0);
        T[] data = (T[]) gson.fromJson(content, arrayObject.getClass());
        List<T> out = list(data);
        return out;
	}
	
	
	public static Gson gsonWithFullDateFormat(){
		return gsonWithFullDateFormat(false);
	}
	
	public static Gson gsonWithFullDateFormat(boolean prettyPrinting){
		return gsonWithDateFormat(FULL_DATE_FORMAT, prettyPrinting);
	}
	
	public static Gson gsonWithDateFormat(String dateFormat, boolean prettyPrinting){
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(dateFormat);
		if(prettyPrinting) builder.setPrettyPrinting();
		return builder.create();
	}

}
