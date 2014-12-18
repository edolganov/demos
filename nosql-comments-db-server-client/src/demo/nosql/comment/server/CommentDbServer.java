package demo.nosql.comment.server;

import static demo.model.BaseBean.*;
import static demo.nosql.comment.common.RemoteApi.*;
import static demo.util.StreamUtil.*;
import static demo.util.StringUtil.*;
import static demo.util.Util.*;
import static demo.util.json.GsonUtil.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;

import demo.exception.ValidationException;
import demo.nosql.comment.CommentDbImpl;
import demo.nosql.comment.common.AddCommentReq;
import demo.nosql.comment.common.InvalidReqException;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.PageId;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.page.PageConverter;
import demo.socket.server.SocketHandler;
import demo.socket.server.SocketServer;
import demo.util.io.LimitInputStream;
import demo.util.socket.SocketUtil;

public class CommentDbServer implements SocketHandler {
	
	
	public static void main(String[] args) throws Exception {
		
		int port = 12060;
		if(args.length > 0) port = tryParseInt(args[0], port);
		
		int maxThreads = 300;
		if(args.length > 1) maxThreads = tryParseInt(args[1], maxThreads);
		
		String rootPath = "./data";
		if(args.length > 2) rootPath = args[2];
		
		//run and wait server
		CommentDbServer server = new CommentDbServer(port, maxThreads, rootPath);
		server.runWait();
	}
	

	private Log log = getLog(getClass());
	private int port;
	private int maxThreads;
	private String rootPath;
	private PageConverter pageConverter;
	
	private SocketServer serverImpl;
	private CommentDbImpl db;
	
	public CommentDbServer(int port, int maxThreads, String rootPath) {
		this(port, maxThreads, rootPath, new PageConverter());
	}
	
	public CommentDbServer(int port, int maxThreads, String rootPath, PageConverter pageConverter) {
		this.port = port;
		this.maxThreads = maxThreads;
		this.rootPath = rootPath;
		this.pageConverter = pageConverter;
	}
	
	public void runAsync() throws IOException {
		init();
		serverImpl.runAsync();
	}
	
	public void runWait() throws IOException {
		init();
		serverImpl.runWait();
	}
	
	private void init() throws IOException{
		serverImpl = new SocketServer("CommentDbServer", port, maxThreads, this);
		db = new CommentDbImpl(new File(rootPath), pageConverter);
	}
	
	public CommentDbImpl getDb(){
		return db;
	}
	
	public void shutdownAsync(){
		serverImpl.shutdownAsync();
	}
	
	public void shutdownWait(){
		serverImpl.shutdownWait();
	}
	

	@Override
	public void process(Socket openedSocket, InputStream socketIn, OutputStream socketOut, SocketServer owner) throws Throwable {
		
		BufferedReader reader = SocketUtil.getReader(socketIn, "UTF-8");
		BufferedOutputStream os = new BufferedOutputStream(socketOut);
		while( ! owner.wasShutdown()){
			String line = reader.readLine();
			if(line == null || owner.wasShutdown()) break;
			
			//process req
			try {
				
				if(line.startsWith(GET_PAGE)){
					sendPage(os, getReqData(line, GET_PAGE, true));
				}
				
				else if(line.startsWith(ADD_COMMENT_ASYNC)){
					addCommentAsync(os, getReqData(line, ADD_COMMENT_ASYNC, true));
				}
				
				else if(line.equals(ALL_PAGES_COUNT)){
					sendAndFlush(os, db.getAllCommentsPagesCount());
				}
				
				else if(line.startsWith(URL_PAGES_COUNT)){
					String req = getReqData(line, URL_PAGES_COUNT, true);
					sendAndFlush(os, db.getCommentsPagesCount(new Url(req)));
				}
				
				else if(line.equals(URLS_COUNT)){
					sendAndFlush(os, db.getUrlsCount());
				}
				
				else if(line.equals(URLS_ENUM)){
					Enumeration<String> urls = db.getUrlsEnumeration();
					os.write(getBytesUTF8("["));
					boolean first = true;
					while(urls.hasMoreElements()){
						if(!first) os.write(',');
						else first = false;
						os.write(getBytesUTF8(urls.nextElement()));
					}
					os.write(getBytesUTF8("]\n"));
				}
				
				else if(line.equals(IS_FULL)){
					sendAndFlush(os, db.isFull());
				}
				
				else if(line.equals(CONN_COUNT)){
					sendAndFlush(os, serverImpl.getActiveConnectionsCount());
				}
				
				else if(line.startsWith(URLS_STATE_SNAPSHOT)){
					String destFile = getReqData(line, URLS_STATE_SNAPSHOT, false);
					db.createUrlsStateSnapshot(destFile);
					sendAndFlush(os, OK);
				}
				
				else if(line.startsWith(CREATE_BACKUP)){
					String destDir = getReqData(line, CREATE_BACKUP, false);
					db.createBackup(destDir);
					sendAndFlush(os, OK);
				}
				
				
				else throw new InvalidReqException("unknown command to CommentDbServer: "+line);
				
				//success done
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
	

	private void addCommentAsync(OutputStream os, String json)throws Exception {
		AddCommentReq req = defaultGson.fromJson(json, AddCommentReq.class);
		validateState(req);
		Future<Long> future = db.addAsync(new Url(req.url), new Comment(req.userId, req.content), 0);
		future.get();
		sendAndFlush(os, OK);
	}

	private void sendPage(OutputStream os, String req) throws IOException {
		int sepIndex = req.indexOf('-');
		if(sepIndex < 1) throw new InvalidReqException("no separator in index-url req: "+req);
		
		int index = tryParseInt(req.substring(0, sepIndex), -1);
		if(index < 0) throw new InvalidReqException("invalid index in index-url req: "+req);
		
		String url = req.substring(sepIndex+1);
		if( ! hasText(url)) throw new InvalidReqException("no url in index-url req: "+req);
		
		
		LimitInputStream pageStream = db.getPageStream(new PageId(url, index));
		if(pageStream == null){
			sendAndFlush(os, NULL);
			return;
		} 
		try {
			
			//send resp info
			os.write(getBytesUTF8(FILE+(pageStream.limit+1)+"\n"));
			//send stream content (expected no '\n' in it)
			copy(pageStream, os, 8192, false);
			os.write('\n');
			os.flush();
			
		}finally {
			pageStream.close();
		}
		
	}
	
	
	
	
	private static String getReqData(String line, String prefix, boolean notNull){
		String data = line.length() > prefix.length()? line.substring(prefix.length()) : null;
		if( ! hasText(data)) {
			if(notNull)throw new InvalidReqException("empty command's data: "+line);
			else return null;
		}
		return data;
	}
	
	private static String validationError(ValidationException e) throws IOException {
		return VALIDATION_ERROR+e;
	}
	
	private static void sendAndFlush(OutputStream os, Object obj) throws IOException{
		String resp = String.valueOf(obj);
		if( ! resp.endsWith("\n")) resp += "\n";
		os.write(getBytesUTF8(resp));
		os.flush();
	}
	
	
	

}
