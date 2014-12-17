package demo.util.file;

import java.io.File;
import java.util.Comparator;

public class FileSortDesc implements Comparator<File>{

	@Override
	public int compare(File a, File b) {
		return -1 * a.getName().compareTo(b.getName());
	}

}
