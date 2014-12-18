import java.io.File;

import demo.nosql.comment.CommentDbImpl;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.page.PageConverter;
import demo.util.StreamUtil;
import demo.util.io.LimitInputStream;


public class NoSql_CommentsDB_Demo {
	
	public static void main(String[] args) throws Exception {
		
		Url url1 = new Url("url1");
		Url url2 = new Url("url2");
		Url url3 = new Url("url3");
		
		File commentDir = new File("./test-out/demo-"+System.currentTimeMillis());
		System.out.println("Comments files store in " + commentDir.getAbsolutePath());
		
		//create db
		int commentsInPage = 2;
		int pagesInFile = 2;
		CommentDbImpl db = new CommentDbImpl(commentDir, new PageConverter(commentsInPage, pagesInFile));
		
		//store comments
		db.addAsync(url1, new Comment(1, "some text 1"), 0).get();
		db.addAsync(url2, new Comment(1, "some text 2"), 0).get();
		db.addAsync(url3, new Comment(1, "some text 3"), 0).get();
		db.addAsync(url1, new Comment(1, "some text 4"), 0).get();
		
		
		System.out.println("Urls count: "+db.getUrlsCount());
		
		
		//read comments in json
		LimitInputStream url1PageStream = db.getPageStream(url1, 0);
		String jsonUrl1Comments = StreamUtil.streamToStr(url1PageStream);
		System.out.println("Url1 comments: " + jsonUrl1Comments);
		
		
		db.close();
	}

}
