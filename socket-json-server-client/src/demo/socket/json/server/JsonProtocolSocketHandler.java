package demo.socket.json.server;


import static demo.util.StringUtil.*;
import static demo.util.Util.*;
import static demo.util.json.GsonUtil.*;
import static demo.util.model.SecureProvider.*;
import demo.socket.json.common.InvalidReqException;
import demo.socket.json.common.ValidationException;
import demo.socket.server.SocketHandler;
import demo.socket.server.SocketServer;
import demo.util.model.SecureProvider;
import demo.util.socket.SocketUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;



public class JsonProtocolSocketHandler implements SocketHandler {
	
	public static final String PROTOCOL_PREFIX = "sckt_jsn:";
	public static final String OK = "OK:";
	public static final String VALIDATION_ERROR = "VALIDATION_ERROR:";
	public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR:";
	
	private Log log = getLog(getClass());
	
	private Map<Class<?>, ReqController<? super Object, ?>> controllers;
	private SecureProvider secureProvider;
	
	public JsonProtocolSocketHandler(SecureProvider secureProvider) {
		this(secureProvider, false);
	}
	
	public JsonProtocolSocketHandler(SecureProvider secureProvider, boolean threadSafe) {
		
		this.secureProvider = secureProvider == null? DUMMY_IMPL : secureProvider;
		
		if(!threadSafe) controllers = new HashMap<>();
		else controllers = new ConcurrentHashMap<>();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void put(Class<T> type, ReqController<T, ?> controller){
		controllers.put((Class)type, (ReqController)controller);
	}
	
	public void remove(Class<?> type){
		controllers.remove(type);
	}

	@Override
	public void process(Socket openedSocket, InputStream socketIn, OutputStream socketOut, SocketServer owner) throws Throwable {
		
		SocketAddress remoteAddress = openedSocket.getRemoteSocketAddress();
		
		BufferedReader reader = SocketUtil.getReaderUTF8(socketIn);
		BufferedOutputStream os = new BufferedOutputStream(socketOut);
		while( ! owner.wasShutdown()){
			
			String line = reader.readLine();
			if(line == null || owner.wasShutdown()) break;
			
			//process req
			try {
				
				try {
					line = secureProvider.decode(line);
				}catch(Throwable t){
					throw new InvalidReqException("decode exception: "+t.getMessage());
				}
				
				if( ! line.startsWith(PROTOCOL_PREFIX)) throw new InvalidReqException("unknown protocol: "+line);
				
				String data = line.substring(PROTOCOL_PREFIX.length());
				int typeSepIndex = data.indexOf(':');
				if(typeSepIndex < 1) throw new InvalidReqException("no data type in req: "+line);
				
				String typeStr = data.substring(0, typeSepIndex);
				Class<?> type = null;
				try {
					type = Class.forName(typeStr);
				}catch (Exception e) {
					throw new InvalidReqException("not java class: "+typeStr);
				}
				
				ReqController<? super Object, ?> controller = controllers.get(type);
				if(controller == null) throw new InvalidReqException("no controller for type: "+type);
				
				Object req = null;
				try {
					
					String jsonReq = data.substring(typeSepIndex+1);
					
					if(hasText(jsonReq)) req = defaultGson.fromJson(jsonReq, type);
				}catch (Exception e) {
					throw new InvalidReqException("invalid json data in req: "+data);
				}
				
				//process
				Object resp = controller.processReq(req, remoteAddress);
				
				//send resp
				String jsonResp = resp == null? "" : defaultGson.toJson(resp);
				
				writeLine(os, OK + controller.respType + ":" + jsonResp);
				os.flush();
				
			}catch (Exception e) {
				//connection closed
				if(e instanceof SocketException) throw e;
				//validation error
				else if(e instanceof ValidationException) sendAndFlush(os, validationError((ValidationException) e));
				//unexpected error
				else {
					log.error("unexpected exception: ", e);
					sendAndFlush(os, UNEXPECTED_ERROR+e);
				}
			}
		}
	}
	
	
	private void sendAndFlush(OutputStream os, Object obj) throws IOException{
		String resp = String.valueOf(obj);
		writeLine(os, resp);
		os.flush();
	}
	
	private void writeLine(OutputStream os, String resp) throws IOException {
		
		//to normal
		if(resp.endsWith("\n")) resp = resp.substring(0, resp.length()-1);
		
		//secure
		resp = secureProvider.encode(resp);
		if(resp.indexOf('\n') > 0) throw new IOException("secureProvider encoded msg with '\n' char");
		resp += "\n";
		
		//write
		os.write(getBytesUTF8(resp));
	}
	
	
	private static String validationError(ValidationException e) throws IOException {
		return VALIDATION_ERROR+e.getMessage();
	}
	

}
