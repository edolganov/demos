package demo.util;

import java.io.File;

import org.junit.Test;

import test.BaseTest;
import static demo.util.FileUtil.*;

public class FileUtilTest extends BaseTest{
	
	@Test
	public void test_replace() throws Exception{
		
		String content1 = "content1";
		String content2 = "content2";
		
		File file1 = new File(TEST_DIR, "test1.txt");
		File file2 = new File(TEST_DIR, "test2.txt");
		
		replaceFileUTF8(file1, content1);
		assertEquals(content1, readFileUTF8(file1));
		
		writeFileUTF8(file2, content1);
		assertEquals(content1, readFileUTF8(file2));
		replaceFileUTF8(file2, content2);
		assertEquals(content2, readFileUTF8(file2));
		
	}

}
