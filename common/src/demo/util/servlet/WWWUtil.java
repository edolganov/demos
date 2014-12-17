package demo.util.servlet;

import static demo.util.Util.*;

import java.util.List;

public class WWWUtil {
	
	private static class WWWUrlBuffer {
		String protocol;
		String other;
		public WWWUrlBuffer(String protocol, String other) {
			super();
			this.protocol = protocol;
			this.other = other;
		}
		@Override
		public String toString() {
			return toString(true);
		}
		public String toString(boolean withWww) {
			StringBuilder sb = new StringBuilder();
			if(protocol != null) sb.append(protocol);
			if(withWww) sb.append("www.");
			if(other != null) sb.append(other);
			return sb.toString();
		}
	}
	
	
	
	public static List<String> getWWW_and_short_fromUrl(String url){
		WWWUrlBuffer buffer = getWWWUrlBuffer(url);
		if(buffer == null) return null;
		return list(buffer.toString(false), buffer.toString(true));
	}
	
	public static boolean hasWWWPrefix(String url){
		if(url == null) return false;
		return url.startsWith("http://www.") || url.startsWith("www.") || url.startsWith("https://www.");
	}
	
	public static String getUrlWithWWW(String url){
		WWWUrlBuffer buffer = getWWWUrlBuffer(url);
		if(buffer == null) return null;
		return buffer.toString(true);
	}
	
	public static String getUrlWithoutWWW(String url){
		WWWUrlBuffer buffer = getWWWUrlBuffer(url);
		if(buffer == null) return null;
		return buffer.toString(false);
	}
	
	
	private static WWWUrlBuffer getWWWUrlBuffer(String url){
		if(url == null) return null;
		String protocol = null;
		if(url.startsWith("http://")){
			protocol = "http://";
			url = url.substring("http://".length());
		}
		else if(url.startsWith("https://")){
			protocol = "https://";
			url = url.substring("https://".length());
		}
		if(url.startsWith("www.")){
			url = url.substring("www.".length());
		}
		return new WWWUrlBuffer(protocol, url);
	}

}
