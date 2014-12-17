package demo.socket.json.client;


import static demo.socket.json.server.JsonProtocolSocketHandler.*;
import static demo.util.Util.*;
import static demo.util.json.GsonUtil.*;
import static demo.util.model.SecureProvider.*;
import demo.socket.json.common.InvalidRespException;
import demo.socket.json.common.UnexpectedServerException;
import demo.socket.json.common.ValidationException;
import demo.socket.pool.SocketConn;
import demo.socket.pool.SocketConnHandler;
import demo.util.model.SecureProvider;

import java.io.IOException;


final class JsonProtocolConnHandler extends SocketConnHandler<Object> {
	
	private final String reqType;
	private final Object data;
	private final SecureProvider secureProvider;

	public JsonProtocolConnHandler(Class<?> type, Object data, SecureProvider secureProvider) {
		if(type == null) throw new IllegalArgumentException("type must be not null");
		this.reqType = type.getName();
		this.data = data;
		this.secureProvider = secureProvider == null? DUMMY_IMPL : secureProvider;
	}

	@Override
	public Object handle(SocketConn c) throws IOException {
		
		String req = PROTOCOL_PREFIX + reqType+":";
		if(data != null) req += defaultGson.toJson(data);
		req = secureProvider.encode(req);
		
		//send
		c.getWriter().println(req);
		
		//get resp
		String resp = c.getReader().readLine();
		
		if(!hasText(resp)) throw new InvalidRespException("empty resp");
		
		try {
			resp = secureProvider.decode(resp);
		}catch(Throwable t){
			throw new InvalidRespException("decode exception: "+t.getMessage());
		}
		
		if(resp.startsWith(OK)){
			String data = resp.substring(OK.length());
			
			int typeSepIndex = data.indexOf(':');
			if(typeSepIndex < 1) throw new InvalidRespException("unknown resp type: "+data);
			
			Class<?> respType = null;
			try {
				String typeStr = data.substring(0, typeSepIndex);
				respType = Class.forName(typeStr);
			}catch (Exception e) {
				throw new InvalidRespException("unknown resp type: "+data);
			}
			
			String respJson = data.substring(typeSepIndex+1);
			if( ! hasText(respJson)) return null;
			else return defaultGson.fromJson(respJson, respType);
			
		}
		if(resp.startsWith(VALIDATION_ERROR)){
			throw new ValidationException(resp.substring(VALIDATION_ERROR.length()));
		}
		if(resp.startsWith(UNEXPECTED_ERROR)){
			throw new UnexpectedServerException(resp.substring(UNEXPECTED_ERROR.length()));
		}
		throw new InvalidRespException("unknown resp: "+resp); 
	}
}