package performance;

//import redis.clients.jedis.Jedis;

/**
 * Tests for x32
 * <pre>
 * Urls count - pages count - RAM (MB)
 * -----------------------------------
 * 800_000		3		2000 (DEAD)
 * 100_000		3		230
 * 10_000		6		30 
 * 10_000		100		75 <---------------- target
 * 
 * </pre>
 */
public class RedisUrlsMapSize {
	
	public static void main(String[] args) {
		
//		Jedis jedis = new Jedis("localhost");
//		
//		for (int i = 0; i < 1000_000; i++) {
//			String url = createStr('z', 1993)+"-"+i;
//			StringBuilder data = new StringBuilder("[");
//			for (int j = 0; j < 3; j++) {
//				if(j > 0) data.append(",");
//				data.append("{\"fileIndex\":"+0);
//				data.append(" \"offset\":"+178257920L);
//				data.append(" \"limit\":"+32000+"}");
//			}
//			data.append("]");
//			jedis.set(url, data.toString());
//			if(i>0 && (i+1) % 1000 == 0) System.out.println("created " + (i + 1) + " urls");
//		}
//		
//		jedis.disconnect();
//		
//		System.out.println("done");
	}

}
