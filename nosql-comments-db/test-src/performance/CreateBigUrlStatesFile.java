package performance;

import java.io.File;
import java.io.IOException;

import demo.nosql.comment.model.CommentsPage;
import demo.nosql.comment.url.UrlLineStorage;
import demo.util.Util;


/**
 * States count - file size 
 * 10_000 - 56MB
 */
public class CreateBigUrlStatesFile {
	
	
	public static void main(String[] args) throws IOException {
		
		File dir = new File("./test-out/big-urls-state-"+Util.randomUUID());
		dir.mkdirs();
		
		UrlLineStorage storage = new UrlLineStorage(dir);
		for (int i = 0; i < 100_000; i++) {
			String url = "url-"+(i + 1);
			storage.createLine(0, url, new CommentsPage(11, 22));
			
			if(i>0 && (i+1) % 1000 == 0) System.out.println("created " + (i + 1) + " urls");
		}
		
		System.out.println("done");
		
	}

}
