package demo.socket.json.server;

import demo.util.ReflectionsUtil;

import java.net.SocketAddress;

public abstract class ReqController<I, O> {
	
	public final String respType;
	
	public ReqController(){
		Class<?> respType = ReflectionsUtil.getActualArgType(getClass(), 1);
		this.respType = respType.getName();
	}
	
	public ReqController(Class<O> respType){
		this.respType = respType.getName();
	}
	
	public abstract O processReq(I data, SocketAddress remoteAddress) throws Exception;

}
