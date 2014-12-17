package demo.util.file;

import java.io.File;
import java.util.Comparator;

public class FileSortAsc implements Comparator<File>{

	@Override
	public int compare(File a, File b) {
		return a.getName().compareTo(b.getName());
	}

}
