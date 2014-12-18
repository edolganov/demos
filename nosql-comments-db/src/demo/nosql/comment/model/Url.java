package demo.nosql.comment.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import demo.exception.comment.InvalidUrlException;
import demo.util.StringUtil;
import static java.util.Collections.*;
import static demo.util.Util.*;

public class Url {
	
	public static final int MAX_URL_SIZE = 2000;
	public static final String CHROME_PROTOCOL = "chrome://";
	
	public final String val;
	public final List<String> tokens;
	
	public final String hostPart;
	public final String pathPart;
	public final String queryPart;
	public final String refPart;
	
	public Url(String urlReq){
		
		if( ! hasText(urlReq)) throw new InvalidUrlException(urlReq);
		urlReq = StringUtil.removeAll(urlReq, "\n\r\t\0 ");
		
		String specialProtocol = null;
		if(urlReq.startsWith(CHROME_PROTOCOL)){
			specialProtocol = "chrome";
			urlReq = urlReq.substring(CHROME_PROTOCOL.length());
		}
		
		if(urlReq.length() > MAX_URL_SIZE) urlReq = urlReq.substring(0, MAX_URL_SIZE);
		if( ! urlReq.contains("://")) urlReq = "http://"+urlReq;
		
		URL parsed = null;
		try {
			parsed = new URL(urlReq);
		}catch (Exception e) {
			throw new InvalidUrlException(urlReq);
		}
		
		StringBuilder sb = new StringBuilder();
		tokens = new ArrayList<String>();
		
		String protocol = parsed.getProtocol();
		if(protocol != null){
			protocol = protocol.toLowerCase();
			if(protocol.equals("http") || protocol.equals("https")) protocol = null;
		}
		if(specialProtocol != null) protocol = specialProtocol;
		if(protocol != null) sb.append(protocol).append("://");
		

		String host = parsed.getHost();
		host = host.toLowerCase();
		if(host.startsWith("www.")){
			host = host.substring("www.".length());
		}
		sb.append(host);
		
		
		int port = parsed.getPort();
		if(port > 0 && port != 80 && port != 443){
			sb.append(':').append(port);
		} else {
			port = 0;
		}
		hostPart = sb.toString();
		tokens.add(hostPart);
		
		//add subdomains mask tokens
		String fullHost = host;
		if(port > 0) fullHost += ":" + port;
		
		ArrayList<String> domainElems = new ArrayList<>();
		StringTokenizer hostElems = new StringTokenizer(fullHost,".");
		while(hostElems.hasMoreTokens()){
			domainElems.add(hostElems.nextToken());
		}
		//create masks
		if(domainElems.size() > 1){
			String curMask = "";
			for (int i = domainElems.size()-1; i > -1; i--) {
				curMask = "." + domainElems.get(i) + curMask;
				tokens.add(curMask);
			}
		}

		
		String path = parsed.getPath();
		if( ! isEmpty(path)){
			if(path.equals("/")) path = null;
			else if(path.endsWith("/")) path = path.substring(0, path.length()-1);
		} else {
			path = null;
		}
		if( ! isEmpty(path)) {
			String pathElem;
			StringTokenizer pathElems = new StringTokenizer(path,"/");
			while(pathElems.hasMoreTokens()){
				pathElem = pathElems.nextToken();
				sb.append('/').append(pathElem);
				tokens.add(sb.toString());
			}
		}
		pathPart = path;
		
		
		
		String query = parsed.getQuery();
		if( ! isEmpty(query)) {
			query = sortQuery(query);
			sb.append('?').append(query);
			tokens.add(sb.toString());
		} else {
			query = null;
		}
		queryPart = query;
		
		
		String ref = parsed.getRef();
		if( ! isEmpty(ref)) {
			sb.append('#').append(ref);
			tokens.add(sb.toString());
		} else {
			ref = null;
		}
		refPart = ref;
		
		val = sb.toString();
		
	}


	public Url(Url other) {
		this.val = other.val;
		this.tokens = other.tokens;
		
		this.hostPart = other.hostPart;
		this.pathPart = other.pathPart;
		this.queryPart = other.queryPart;
		this.refPart = other.refPart;
	}
	
	
	
	public String getHostWithPath(){
		StringBuilder sb = new StringBuilder(hostPart);
		if(pathPart != null) sb.append(pathPart);
		return sb.toString();
	}
	
	public String getFullPath() {
		StringBuilder sb = new StringBuilder();
		if(pathPart != null) sb.append(pathPart);
		if(queryPart != null) sb.append('?').append(queryPart);
		if(refPart != null) sb.append('#').append(refPart);
		return sb.toString();
	}
	
	
	@Override
	public String toString() {
		return val;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		Url other = (Url) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	
	
	public static String sortQuery(String query) {
		if(isEmpty(query)) return query;
		
		ArrayList<String> pairs = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(query,"&");
		while(st.hasMoreTokens()) pairs.add(st.nextToken());
		sort(pairs);
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String pair : pairs) {
			if( ! first) sb.append('&');
			else first = false;
			sb.append(pair);
		}
		return sb.toString();
	}
	
	

}