package demo.nosql.comment.client;

import static demo.nosql.comment.CommentDbCommon.*;
import static demo.nosql.comment.common.RemoteApi.*;
import static demo.util.StringUtil.*;
import static demo.util.Util.*;
import static demo.util.json.GsonUtil.*;
import static java.util.Collections.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import demo.exception.ValidationException;
import demo.nosql.comment.CommentDb;
import demo.nosql.comment.client.exception.ConnectionProblemException;
import demo.nosql.comment.common.AddCommentReq;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentId;
import demo.nosql.comment.model.PageId;
import demo.nosql.comment.model.Url;
import demo.socket.pool.SocketConn;
import demo.socket.pool.SocketConnHandler;
import demo.socket.pool.SocketsPool;
import demo.util.StringUtil;
import demo.util.concurrent.ExecutorsUtil;
import demo.util.io.LimitInputStream;

public class CommentDbClient implements CommentDb {
	
	private SocketsPool socketsPool;
	private ExecutorService writeThread = ExecutorsUtil.newSingleThreadExecutor("CommentDbClient");
	
	public CommentDbClient(Properties props) {
		this(
			props.getProperty("comments.remote.host", "localhost"), 
			tryParseInt(props.getProperty("comments.remote.port"), 12060), 
			tryParseInt(props.getProperty("comments.remote.maxConns"), 30), 
			tryParseInt(props.getProperty("comments.remote.idleConns"), 30));
	}
	

	public CommentDbClient(String host, int port, int maxConnections, int idleConnections) {
		this(host, port, maxConnections, idleConnections, 10000);
	}
	
	public CommentDbClient(String host, int port, int maxConnections, int idleConnections, Integer socketSoTimeout) {
		socketsPool = new SocketsPool(host, port);
		socketsPool.setPoolMaximumActiveConnections(maxConnections);
		socketsPool.setPoolMaximumIdleConnections(idleConnections);
		socketsPool.setSocketSoTimeoutForNewConn(socketSoTimeout);
	}
	
	abstract class ClientConnHandler<T> extends SocketConnHandler<T> {
		@Override
		public T onException(SocketConn c, Throwable t) throws IOException {
			if(t instanceof SocketException || t instanceof SocketTimeoutException){
				t = new ConnectionProblemException(socketsPool.getHost(), socketsPool.getPort(), t);
			}
			return super.onException(c, t);
		}
	}

	@Override
	public Future<Long> addAsync(final Url url, final Comment comment, int pageBlockIndexForNew) {
		return writeThread.submit(new Callable<Long>() {
			@Override
			public Long call() throws Exception {
				return socketsPool.invoke(new ClientConnHandler<Long>() {
					
					@Override
					public Long handle(SocketConn c) throws IOException {
						c.getWriter().println(ADD_COMMENT_ASYNC+defaultGson.toJson(new AddCommentReq(url, comment)));
						c.getReader().readLine();
						return 0L;
					}
					
				});
			}
		});
	}
	
	@Override
	public LimitInputStream getPageStream(PageId pageId) throws IOException {
		return getPageStream(pageId.url, pageId.pageIndex);
	}
	
	
	@Override
	public LimitInputStream getPageStream(final Url url, final int pageIndex) throws IOException {
		return socketsPool.invoke(new ClientConnHandler<LimitInputStream>() {
			@Override
			public LimitInputStream handle(SocketConn c) throws IOException {
				c.getWriter().println(GET_PAGE+pageIndex+"-"+url);
				
				BufferedReader reader = c.getReader();
				String resp = reader.readLine();
				int contentLength = -1;
				if(resp.equals(NULL)) return null;
				if(resp.startsWith(FILE)) contentLength = tryParseInt(resp.substring(FILE.length()), -1);
				if(contentLength == -1) throw new IllegalStateException("unknown server resp: "+resp);
				
				if(contentLength< 0 || contentLength > 35000) throw new IllegalStateException("stream's size is too large, can't read it all to RAM: "+contentLength);
				
				//read page
				String page = reader.readLine();
				return new LimitInputStream(new ByteArrayInputStream(getBytesUTF8(page)), contentLength-1);
			}
		});
	}

	@Override
	public int getAllCommentsPagesCount() throws IOException {

		return socketsPool.invoke(new ClientConnHandler<Integer>() {
			@Override
			public Integer handle(SocketConn c) throws IOException {
				c.getWriter().println(ALL_PAGES_COUNT);
				return Integer.parseInt(c.getReader().readLine());
			}
		});
	}

	@Override
	public int getCommentsPagesCount(final Url url) throws IOException {
		return socketsPool.invoke(new ClientConnHandler<Integer>() {
			@Override
			public Integer handle(SocketConn c) throws IOException {
				c.getWriter().println(URL_PAGES_COUNT+url.val);
				return Integer.parseInt(c.getReader().readLine());
			}
		});
	}

	@Override
	public int getUrlsCount() throws IOException {
		return socketsPool.invoke(new ClientConnHandler<Integer>() {
			@Override
			public Integer handle(SocketConn c) throws IOException {
				c.getWriter().println(URLS_COUNT);
				return Integer.parseInt(c.getReader().readLine());
			}
		});
	}
	

	@Override
	public Enumeration<String> getUrlsEnumeration() throws IOException {
		return socketsPool.invoke(new ClientConnHandler<Enumeration<String>>() {
			@Override
			public Enumeration<String> handle(SocketConn c) throws IOException {
				c.getWriter().println(URLS_ENUM);
				String line = c.getReader().readLine();
				if(NULL.equals(line)) return null;
				List<String> list = StringUtil.strToList(line, ',', '[', ']');
				return isEmpty(list)? null : enumeration(list);
			}
		});
	}
	
	@Override
	public boolean isFull() throws IOException {
		return socketsPool.invoke(new ClientConnHandler<Boolean>() {
			@Override
			public Boolean handle(SocketConn c) throws IOException {
				c.getWriter().println(IS_FULL);
				return Boolean.parseBoolean(c.getReader().readLine());
			}
		});
	}


	
	@Override
	public void close() throws IOException {}
	
	
	public int connectionsCount() throws IOException{
		return socketsPool.invoke(new ClientConnHandler<Integer>() {
			@Override
			public Integer handle(SocketConn c) throws IOException {
				c.getWriter().println(CONN_COUNT);
				return tryParseInt(c.getReader().readLine(), -1);
			}
		});
	}
	
	public void forceCloseAll() {
		socketsPool.forceCloseAll();
	}


	@Override
	public void createUrlsStateSnapshot(String resultFilePath) throws IOException {
		final String req = resultFilePath != null? resultFilePath : "";
		socketsPool.invoke(new ClientConnHandler<Void>() {
			@Override
			public Void handle(SocketConn c) throws IOException {
				c.getWriter().println(URLS_STATE_SNAPSHOT+req);
				checkOkResp(c);
				return null;
			}
		});
	}
	
	@Override
	public void createBackup(String backupDirPath) throws IOException {
		final String req = backupDirPath != null? backupDirPath : "";
		socketsPool.invoke(new ClientConnHandler<Void>() {
			@Override
			public Void handle(SocketConn c) throws IOException {
				c.getWriter().println(CREATE_BACKUP+req);
				checkOkResp(c);
				return null;
			}
		});
	}
	
	
	
	
	
	private static void checkOkResp(SocketConn c) throws IOException{
		String line = c.getReader().readLine();
		if(line == null) throw new IOException("server cancel connection");
		
		if(line.equals(OK)) return;
		else if(line.startsWith(VALIDATION_ERROR)) throw new ValidationException(line.substring(VALIDATION_ERROR.length()));
		else if(line.startsWith(UNEXPECTED_ERROR)) throw new IllegalStateException(line.substring(UNEXPECTED_ERROR.length()));
		else throw new IllegalStateException("unknown server unswer: "+line);
	}


	@Override
	public Comment getComment(CommentId commentId) throws IOException {
		return getCommentFromStream(getPageStream(commentId.getPageId()), commentId);
	}



	

}
