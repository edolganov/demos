package demo.util.string;



import demo.util.string.WordsCounter.IndexesStorage;
import demo.util.string.WordsCounter.WordStat;

import java.io.IOException;



import org.junit.Test;

import test.BaseTest;

@org.junit.Ignore
public class IndexesStorageTest extends BaseTest {
	
	
	@Test
	public void test_put_get() throws IOException{
		
		String word1 = "один";
		String word2 = "second";
		String word3 = "word3";
		
		IndexesStorage storage = new IndexesStorage(TEST_DIR, 100);
			
		assertNull(storage.getFromStorage(word1));
		storage.putToStorage(new WordStat(word1, 1));
		{
			WordStat stat = storage.getFromStorage(word1);
			assertNotNull(stat);
			assertEquals(word1, stat.word);
			assertEquals(1, stat.count());
		}
		
		
		storage.putToStorage(new WordStat(word1, 2));
		storage.putToStorage(new WordStat(word2, 1));
		{
			WordStat stat = storage.getFromStorage(word1);
			assertNotNull(stat);
			assertEquals(word1, stat.word);
			assertEquals(2, stat.count());
		}
		{
			WordStat stat = storage.getFromStorage(word2);
			assertNotNull(stat);
			assertEquals(word2, stat.word);
			assertEquals(1, stat.count());
		}
		assertNull(storage.getFromStorage(word3));
		
	}
	

}
