package demo.util;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

public class NetUtil {
	
	public static String sendRequest(String url) throws Exception {
		return sendRequest(url, 30000, 30000);
	}
	
	public static String sendRequest(String url, int connTimeout, int readTimeout) throws Exception {
		
		HttpURLConnection conn=null;
        try{
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setConnectTimeout(connTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setUseCaches(false);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            //It may happen due to keep-alive problem http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
            if (code==-1) {
            	throw new IllegalStateException("NetUtil: conn.getResponseCode() return -1");
            }
            
            InputStream is = new BufferedInputStream(conn.getInputStream());
            String enc = conn.getHeaderField("Content-Encoding");
            if(enc !=null && enc.equalsIgnoreCase("gzip")){
                is = new GZIPInputStream(is);
            }
            
            String response = StreamUtil.streamToStr(is);
            return response;
        }
        finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
		
	}
	
	public static String sendPost(String url) throws Exception {
		return sendPost(url, null);
	}
	
	public static String sendPost(String url, Map<?,?> postParams) throws Exception {
		return sendPost(url, postParams, 30000, 30000);
	}
	
	public static String sendPost(String url, Map<?,?> postParams, int connTimeout, int readTimeout) throws Exception {
		
		HttpURLConnection conn=null;
        try{
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setConnectTimeout(connTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            
            String agent = System.getProperty("http.agent");
			if(Util.hasText(agent)){
            	conn.setRequestProperty("User-Agent", agent);
            }
            
            if( Util.isEmpty(postParams)){
            	conn.setDoOutput(false);
            } else {
            	
            	// Send post request
            	conn.setDoOutput(true);
            	
            	//params
            	StringBuilder sb = new StringBuilder();
            	boolean first = true;
            	for(Entry<?,?> entry : postParams.entrySet()){
            		if(!first)sb.append('&');
            		first = false;
            		sb.append(URLEncoder.encode(String.valueOf(entry.getKey()), "UTF8"))
            		.append('=')
            		.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF8"));
            	}
        		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        		wr.writeBytes(sb.toString());
        		wr.flush();
        		wr.close();
            }
            
            int code = conn.getResponseCode();
            //It may happen due to keep-alive problem http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
            if (code==-1) {
            	throw new IllegalStateException("NetUtil: conn.getResponseCode() return -1");
            }
            
            InputStream is = new BufferedInputStream(conn.getInputStream());
            String enc = conn.getHeaderField("Content-Encoding");
            if(enc !=null && enc.equalsIgnoreCase("gzip")){
                is = new GZIPInputStream(is);
            }
            
            String response = StreamUtil.streamToStr(is);
            return response;
        }
        finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
 
	}

}
