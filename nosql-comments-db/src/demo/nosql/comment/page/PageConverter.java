package demo.nosql.comment.page;

import java.util.Date;

import org.apache.commons.logging.Log;

import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.FileInfo;
import demo.util.ExceptionUtil;
import static demo.util.ArrayUtil.*;
import static demo.util.StringUtil.*;
import static demo.util.Util.*;
import static demo.util.json.GsonUtil.*;

public class PageConverter {
	
	private static final Log log = getLog(PageConverter.class);

	public static final int DEFAULT_COMMENTS_IN_PAGE = 50;
	public static final int DEFAULT_PAGES_IN_FILE = 10_000;
	public static final char SEPARATOR = ',';
	public static final char LINE_END = '\n';
	
	
	
	public final int commentsInPage;
	public final int pagesInFile;
	
	public final int commentBytesCount;
	public final int pageBytesCount;
	public final long fileBytesCount;
	
	
	//default
	public PageConverter() {
		this(tryParseInt(System.getProperty("sc.comments_in_page"), DEFAULT_COMMENTS_IN_PAGE), 
			tryParseInt(System.getProperty("sc.pages_in_file"), DEFAULT_PAGES_IN_FILE));
	}

	public PageConverter(int bigCommentsInPage, int pagesInFile) {
		this.commentsInPage = bigCommentsInPage;
		this.pagesInFile = pagesInFile;
		
		this.commentBytesCount = calculateCommentBytesCount();
		
		//comments + separators: {...},{...},{...}/n
		this.pageBytesCount = commentBytesCount*bigCommentsInPage + bigCommentsInPage;
		this.fileBytesCount = pageBytesCount*pagesInFile;
		
	}
	
	public boolean isFull(FileInfo file){
		return file.length >= fileBytesCount;
	}
	
	
	public static class NewPageToBytesResult {
		public final byte[] pageBytes;
		public final byte[] commentBytes;
		public NewPageToBytesResult(byte[] pageBytes, byte[] commentBytes) {
			super();
			this.pageBytes = pageBytes;
			this.commentBytes = commentBytes;
		}
	}
	
	public NewPageToBytesResult newPageToBytes(Comment c){
		
		byte[] commentBytes = commentToBytes(c);
		byte[] pageBytes = new byte[pageBytesCount];
		copyFromSmallToBig(commentBytes, pageBytes, 0);
		pageBytes[pageBytesCount-1] = LINE_END;
		return new NewPageToBytesResult(pageBytes, commentBytes);
		
	}
	
	
	public static class NextCommentToBytesResult {
		public final byte[] nextCommentBytes;
		public final byte[] commentBytes;
		public NextCommentToBytesResult(byte[] nextCommentBytes, byte[] commentBytes) {
			super();
			this.nextCommentBytes = nextCommentBytes;
			this.commentBytes = commentBytes;
		}
	}
	
	public NextCommentToBytesResult nextCommentToBytes(Comment c){
		byte[] commentBytes = commentToBytes(c);
		byte[] nextCommentBytes = new byte[commentBytes.length+1];
		copyFromSmallToBig(commentBytes, nextCommentBytes, 1);
		nextCommentBytes[0] = SEPARATOR;
		return new NextCommentToBytesResult(nextCommentBytes, commentBytes);
	}
	
	public byte[] commentToBytes(Comment c){
		byte[] bytes = commentToJsonBytes(c);
		if(bytes.length > commentBytesCount) throw new IllegalStateException("comment's json bytes count > "+commentBytesCount+": "+c.getContent());
		return bytes;
	}
	
	
	private static int calculateCommentBytesCount() {
		try {
			Comment c = new Comment(new Date(), Long.MAX_VALUE, createStr('я', Comment.CONTENT_MAX_SIZE));
			return commentToJsonBytes(c).length;
		}catch (Throwable t) {
			log.error("can't calculateCommentBytesCount", t);
			throw ExceptionUtil.getRuntimeExceptionOrThrowError(t);
		}
	}
	
	private static byte[] commentToJsonBytes(Comment c){
		try {
			String json = defaultGson.toJson(c);
			byte[] bytes = getBytesUTF8(json);
			return bytes;
		} catch (Exception e) {
			throw ExceptionUtil.getRuntimeExceptionOrThrowError(e);
		}
	}

}
