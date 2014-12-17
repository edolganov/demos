package demo.socket.json.server;


import demo.socket.server.SocketServer;
import demo.util.model.SecureKeyHolder;
import demo.util.model.SecureProviderImpl;

import java.io.IOException;



public class JsonSocketServer implements SecureKeyHolder {
	
	private SocketServer serverImpl;
	private JsonProtocolSocketHandler socketHandler;
	private final SecureProviderImpl secureProvider; 

	public JsonSocketServer(int port, int maxThreads) {
		this("JsonSocketServer", port, maxThreads);
	}

	public JsonSocketServer(String name, int port, int maxThreads) {
		this.secureProvider = new SecureProviderImpl("JsonSocketServer("+port+")-"+name); 
		this.socketHandler = new JsonProtocolSocketHandler(secureProvider);
		this.serverImpl = new SocketServer(name, port, maxThreads, socketHandler);
	}
	
	@Override
	public void setSecureKey(String key){
		secureProvider.setSecureKey(key);
	}
	
	@Override
	public boolean isSecuredByKey(){
		return secureProvider.isSecuredByKey();
	}
	
	
	public void runAsync() throws IOException {
		serverImpl.runAsync();
	}
	
	public void runWait() throws IOException {
		serverImpl.runWait();
	}
	
	public void shutdownAsync(){
		serverImpl.shutdownAsync();
	}
	
	public void shutdownWait(){
		serverImpl.shutdownWait();
	}
	
	public <T> void putController(Class<T> type, ReqController<T, ?> controller){
		socketHandler.put(type, controller);
	}
	
	public void removeController(Class<?> type){
		socketHandler.remove(type);
	}
	
	

}
