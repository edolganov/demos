package demo.socket.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface SocketHandler {

	void process(Socket openedSocket, InputStream socketIn, OutputStream socketOut, SocketServer owner) throws Throwable;
	
}