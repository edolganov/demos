package demo.socket.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static demo.util.socket.SocketUtil.*;

public abstract class SocketWriterHander implements SocketHandler {

	@Override
	public void process(Socket openedSocket, InputStream socketIn, OutputStream socketOut, SocketServer owner) throws Throwable {
		BufferedReader reader = getReaderUTF8(socketIn);
		PrintWriter writer = getWriterUTF8(socketOut);
		process(openedSocket, reader, writer, owner);
	}
	
	protected abstract void process(Socket openedSocket, BufferedReader socketReader, PrintWriter socketWriter, SocketServer owner) throws Throwable;

}
