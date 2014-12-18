package performance;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import demo.nosql.comment.CommentDbImpl;
import demo.nosql.comment.exception.CommentDBIsFullException;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.page.PageConverter;
import demo.nosql.comment.page.PageStorage;
import static demo.util.ExceptionUtil.*;


/**
 * Read max db
 * Total: 220mb RAM
 */
public class MaxDBRead {
	
	
	
	public static void main(String[] args) throws Exception {
		
		File dir = new File("./test-out/max-db");
		CommentDbImpl db = new CommentDbImpl(dir);
		
		//try add
		try {
			db.addAsync(new Url("test-url"), new Comment("123"), 0).get();
		}catch (ExecutionException e) {
			Throwable t = e.getCause();
			if( ! (t instanceof CommentDBIsFullException)) getExceptionOrThrowError(t);
		} finally {
			db.close();
		}
		
		//try read
		for (int i = 0; i < PageConverter.DEFAULT_PAGES_IN_FILE; i++) {
			for (int j = 0; j < PageStorage.DEFAULT_MAX_FILES_COUNT; j++) {
				InputStream is = db.getPageStream(new Url("url"+i), j);
				if(is == null){
					throw new IllegalStateException("null stream by url "+i+" and page "+j);
				}
				if(j>0 && (j+1) % 100 == 0) System.out.println((i+1)+":\treaded " + (j + 1) + " pages");
			}
			if(i>0 && (i+1) % 1000 == 0) System.out.println("readed " + (i + 1) + " urls");
		}
		
		int urlsCount = db.getUrlsCount();
		int pagesCount = db.getAllCommentsPagesCount();
		long totalComments = PageConverter.DEFAULT_COMMENTS_IN_PAGE * pagesCount;
		System.out.println("done: " +
				"\n urls="+urlsCount +
				"\n pages="+pagesCount +
				"\n comments="+totalComments
				);
		
	}

}
