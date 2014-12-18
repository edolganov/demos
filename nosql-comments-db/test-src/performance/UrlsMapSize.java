package performance;

import java.util.HashMap;

import demo.nosql.comment.model.UrlInfoMock;
import static demo.util.MemoryExtUtil.*;
import static demo.util.StringUtil.*;

/**
 * Tests for x32
 * <pre>
 * Urls count - pages count - RAM (MB)
 * -----------------------------------
 * 300_000		2		1300 (DEAD)
 * 100_000		100		700
 * 10_000		100		70 <---------------- target
 * 
 * </pre>
 */
public class UrlsMapSize {
	
	static final HashMap<String, UrlInfoMock> map = new HashMap<>();
	
	public static void main(String[] args) throws Exception {
		
		long start = getMemoryUse();
		for (int i = 0; i < 100_000; i++) {
			map.put(createStr('z', 1990)+"-"+i, new UrlInfoMock(100));
			if(i>0 && (i+1) % 1000 == 0) System.out.println("created " + (i + 1) + " urls");
		}
		long end = getMemoryUse();
		System.out.println("done: "+getApproximateSize_Mb(start, end));
		
	}

}
