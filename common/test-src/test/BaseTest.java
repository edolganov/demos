package test;



import demo.junit.AssertExt;
import demo.util.FileUtil;

import java.io.File;




import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestName;

import web.MockHttpServletRequest;
import web.MockHttpServletResponse;


@Ignore
public abstract class BaseTest extends AssertExt {
	
	private static final File rootDir = new File("./test-out");
	
	static {
		FileUtil.deleteDirRecursive(rootDir);
		rootDir.mkdirs();
	}
	
	public String TEST_PATH;
	public File TEST_DIR;
	protected boolean createDir = true;
	
	@Rule
	public TestName name = new TestName();
	
	@Before
	public void createDir(){
		TEST_PATH = "./test-out/"+getClass().getSimpleName()+"@"+name.getMethodName()+"__"+System.currentTimeMillis();
		if(createDir){
			TEST_DIR = new File(TEST_PATH);
			TEST_DIR.mkdir();
		}
	}
	
	public String testPath(String path){
		return TEST_PATH+path;
	}
	
	public void assertTestFileExists(String path){
		if( ! path.startsWith("/")) path = "/" + path;
		assertFileExists(testPath(path));
	}
	
	public void assertTestFileNotExists(String path){
		assertFileNotExists(testPath(path));
	}
	
	
	public static MockHttpServletRequest mockReq(){
		return new MockHttpServletRequest();
	}
	
	public static MockHttpServletResponse mockResp(){
		return new MockHttpServletResponse();
	}
	
	
	public static String path(File parent, File... pathElems){
		StringBuilder sb = new StringBuilder();
		sb.append(parent.getPath());
		for(File child : pathElems) {
			sb.append("/").append(child.getName());
		}
		return sb.toString();
	}

}
