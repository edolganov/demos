package demo.nosql.comment;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.Future;

import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.CommentId;
import demo.nosql.comment.model.PageId;
import demo.nosql.comment.model.Url;
import demo.util.io.LimitInputStream;

public interface CommentDb {
	
	public Future<Long> addAsync(final Url url, final Comment c, final int pageBlockIndexForNew);
	
	public int getAllCommentsPagesCount() throws IOException;
	
	public int getCommentsPagesCount(Url url) throws IOException;
	
	public LimitInputStream getPageStream(PageId pageId) throws IOException;
	
	public LimitInputStream getPageStream(Url url, int pageIndex) throws IOException;
	
	public Comment getComment(CommentId commentId) throws IOException;
	
	public int getUrlsCount() throws IOException;
	
	public Enumeration<String> getUrlsEnumeration() throws IOException;
	
	public boolean isFull() throws IOException;
	
	public void createUrlsStateSnapshot(String resultFilePath) throws IOException;
	
	public void createBackup(String backupDirPath) throws IOException;
	
	public void close() throws IOException;
	
	
}
