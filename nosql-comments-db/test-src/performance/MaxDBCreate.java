package performance;

import java.io.File;
import java.util.Date;
import java.util.Random;

import demo.nosql.comment.CommentDbImpl;
import demo.nosql.comment.model.Comment;
import demo.nosql.comment.model.Url;
import demo.nosql.comment.page.PageConverter;
import demo.nosql.comment.page.PageStorage;
import demo.util.StringUtil;
import static demo.nosql.comment.model.Comment.*;


/**
 * Creating 10.000 urls with 500 comments (5.000.0000 total comments count)
 * Result: 
 * 	40MB RAM, 
 * 	3,2GB HDD
 */
public class MaxDBCreate {
	
	
	public static void main(String[] args) throws Exception {
		
		
		
		File dir = new File("./test-out/max-db-"+System.currentTimeMillis());
		dir.mkdirs();
		
		String content = StringUtil.createStr('я', CONTENT_MAX_SIZE);
		Random r = new Random();
		
		CommentDbImpl db = new CommentDbImpl(dir);
		long userId;
		String url;
		for (int i = 0; i < PageConverter.DEFAULT_PAGES_IN_FILE; i++) {
			url = StringUtil.randomStr(5)+i;
			for (int j = 0; j < PageConverter.DEFAULT_COMMENTS_IN_PAGE * PageStorage.DEFAULT_MAX_FILES_COUNT; j++) {
				userId = r.nextInt(2) > 0? 1 : Long.MAX_VALUE;
				db.addAsync(new Url(url), new Comment(new Date(), userId, content), 0).get();
				//if(j>0 && (j+1) % 100 == 0) System.out.println((i+1)+":\tcreated " + (j + 1) + " comments");
			}
			if(i>0 && (i+1) % 100 == 0) System.out.println("created " + (i + 1) + " urls");
		}
		db.close();
		
		System.out.println("done");
		
	}

}
