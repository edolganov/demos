package filescan;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
	
	public List<String> getFilesList(){
		ArrayList<String> out = new ArrayList<String>();
		CodeSource src = FileScanner.class.getProtectionDomain().getCodeSource();
		if (src != null) {
		  URL jar = src.getLocation();
		  //ZipInputStream zip = new ZipInputStream(jar.openStream());
		  out.add(jar.toString());
		}
		return out;
	}
	
	public static void main(String[] args) {
		System.out.println(new FileScanner().getFilesList());
	}

}
