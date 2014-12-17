package demo.util.model;

import static demo.util.Util.*;
import static demo.util.servlet.WebUtil.*;

import java.io.IOException;

import org.apache.commons.logging.Log;

public class SecureProviderImpl implements SecureProvider, SecureKeyHolder {
	
	private static Log log = getLog(SecureProviderImpl.class);
	
	public volatile boolean warnInsecure;
	public final String ownerName;
	
	private volatile String key;
	

	public SecureProviderImpl(String ownerName) {
		this.ownerName = ownerName;
		warnInsecure = tryParseBool(System.getenv("SecureProvider.skipWarnInsecure"), true);
	}

	@Override
	public void setSecureKey(String key){
		
		if((this.key == null && key != null) 
			|| (this.key != null && ! this.key.equals(key))){
			logWarn("UPDATE SECURE KEY"+(key == null? " TO EMPTY!" : ""));
		}
		
		this.key = key;
	}
	
	@Override
	public boolean isSecuredByKey(){
		return key != null;
	}

	@Override
	public String encode(String msg) throws IOException {
		if(key == null) {
			if(warnInsecure) logWarn("INSECURE ENCODE");
			return msg;
		}
		return encodeToken(msg, key);
	}

	@Override
	public String decode(String msg) throws IOException {
		if(key == null) {
			if(warnInsecure) logWarn("INSECURE DECODE");
			return msg;
		}
		return decodeToken(msg, key);
	}
	
	private void logWarn(String msg){
		if( ! log.isWarnEnabled()) return;
		String suffix = ownerName != null? ", owner="+ownerName : "";
		log.warn(msg+suffix);
	}

}
