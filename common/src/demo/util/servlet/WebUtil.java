package demo.util.servlet;


import static demo.util.ExceptionUtil.*;
import static demo.util.StringUtil.*;
import static demo.util.Util.*;
import static demo.util.codec.Base64.*;
import demo.util.crypto.AES128;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



public class WebUtil {
	
	public static final String CSRF_PROTECT_TOKEN = "CSRF_ProtectToken";
	public static final String XML_HTTP_REQUEST_VAL = "XMLHttpRequest";
	public static final String HEADER_X_REQUESTED_WITH = "X-Requested-With";
	
	public static String getClientIp(HttpServletRequest req){
		
		String ipAddress = req.getHeader("x-forwarded-for");
        if (ipAddress == null) {
        	ipAddress = req.getRemoteAddr();
        }
        return ipAddress;
	}
	
	public static String getUserAgent(HttpServletRequest req){
		return req.getHeader("User-Agent");
	}
	
	public static String getReferer(HttpServletRequest req){
		return req.getHeader("Referer");
	}
	
	public static String encodeToken(String token, String key) {
		byte[] encoded = AES128.encode(token, key);
		String result = encodeBase64String(encoded);
		return result;
	}

	
	public static String decodeToken(String encoded, String key) {
		byte[] bytes = decodeBase64(encoded);
		return AES128.decode(bytes, key);
	}
	
	public static String createActivationCode(){
		return randomSimpleId();
	}
	
	public static String createSalt(){
		return randomSimpleId();
	}
	
	
	public static byte[] getHash(String input, String salt) {
		try {
			
			String salted = input + salt;
			MessageDigest md = MessageDigest.getInstance("SHA-512"); //512 - 64bytes
			md.update(getBytesUTF8(salted));
			return md.digest();
			
		}catch (Exception e) {
			throw getRuntimeExceptionOrThrowError(e);
		}
	}
	
	public static String generateRandomPsw(int length) {
		String out = encodeBase64String(randomUUID()).toLowerCase();
		return out.length() > length? out.substring(0, length) : out;
	}
	
	public static boolean hasXReqHeader(HttpServletRequest req){
		return XML_HTTP_REQUEST_VAL.equals(req.getHeader(HEADER_X_REQUESTED_WITH));
	}
	
	public static void createAndSet_CSRF_ProtectToken(HttpSession session){
		session.setAttribute(CSRF_PROTECT_TOKEN, create_CSRF_ProtectToken());
	}
	
	public static String create_CSRF_ProtectToken(){
		return encodeBase64String(randomUUID());
	}
	
	public static String get_CSRF_ProtectTokenFromSession(HttpServletRequest req){
		HttpSession session = req.getSession(false);
		return session == null? null : (String) session.getAttribute(CSRF_PROTECT_TOKEN);
	}
	
	public static void set_CSRF_ProtectTokenCookieFromSession(HttpServletRequest req, HttpServletResponse resp){
		
		String token = get_CSRF_ProtectTokenFromSession(req);
		if(token == null){
			return;
		}
		
		resp.addCookie(cookie(CSRF_PROTECT_TOKEN, token, true, -1));
	}
	
	
	public static boolean isValid_CSRF_ProtectTokenInReq(HttpServletRequest req){
		
		String token = get_CSRF_ProtectTokenFromSession(req);
		if(token == null){
			return true;
		}
		
		String cookieToken = null;
		Cookie[] cookies = req.getCookies();
		if(isEmpty(cookies)) return false;
		for (Cookie cookie : cookies) {
			if(CSRF_PROTECT_TOKEN.equals(cookie.getName())){
				cookieToken = cookie.getValue();
				break;
			}
		}
		
		String reqToken = req.getParameter("token");
		
		return token.equals(cookieToken) && token.equals(reqToken);
	}
	
	public static Cookie findCookie(HttpServletRequest req, String name){
		Cookie[] cookies = req.getCookies();
		if(isEmpty(cookies)) return null;
		for (Cookie cookie : cookies) {
			if(name.equals(cookie.getName())){
				return cookie;
			}
		}
		return null;
	}
	
	public static String findCookieVal(HttpServletRequest req, String name){
		Cookie c = findCookie(req, name);
		return c != null? c.getValue() : null;
	}
	
	
	
	
	
	public static String filterQuery(String query, Set<String> onlyParams, Set<String> removeParams){
		if( isEmpty(query) || (isEmpty(onlyParams) && isEmpty(removeParams))) return query;
		
		boolean isOnlyMode = ! isEmpty(onlyParams);
		List<String> validPairs = new LinkedList<>();
		StringTokenizer st = new StringTokenizer(query, "&");
		String pair;
		int eqIndex;
		String key;
		while(st.hasMoreTokens()){
			pair = st.nextToken();
			if(isEmpty(pair)) continue;
			eqIndex = pair.indexOf('=');
			if(eqIndex < 1) continue;
			key = pair.substring(0, eqIndex);
			if( isOnlyMode && ! onlyParams.contains(key)) continue;
			if( ! isOnlyMode && removeParams.contains(key)) continue;
			validPairs.add(pair);
		}
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(String pairVal : validPairs){
			if(!first) sb.append('&');
			else first = false;
			sb.append(pairVal);
		}
		return sb.toString();
	}

	
	public static Cookie cookie(String name, String val, boolean isHttpOnly, int maxAgeSec){
		return cookie(name, val, isHttpOnly, "/", maxAgeSec);
	}
	
	public static Cookie cookie(String name, String val, boolean isHttpOnly, String path, int maxAgeSec){
		Cookie cookie = new Cookie(name, val);
		cookie.setHttpOnly(isHttpOnly);
		cookie.setPath(path);
		cookie.setMaxAge(maxAgeSec);
		return cookie;
	}
	
	public static Cookie cookie(String name, String val, boolean isHttpOnly, String path, int maxAgeSec, String domain){
		Cookie cookie = new Cookie(name, val);
		cookie.setHttpOnly(isHttpOnly);
		cookie.setPath(path);
		cookie.setMaxAge(maxAgeSec);
		cookie.setDomain(domain);
		return cookie;
	}
	
	public static Cookie deletedCookie(String name){
		return deletedCookie(name, "/");
	}
	
	public static Cookie deletedCookie(String name, String path){
		Cookie cookie = new Cookie(name, "");
		cookie.setPath(path);
		cookie.setMaxAge(0);
		return cookie;
	}
	
	
	public static String toHttps(String httpUrl){
		if(httpUrl.startsWith("http://")){
			return "https://"+httpUrl.substring("http://".length());
		}
		return httpUrl;
	}
	
	public static void forward(HttpServletRequest req, HttpServletResponse resp, String path) throws ServletException, IOException{
		req.getRequestDispatcher(path).forward(req, resp);
	}
}
