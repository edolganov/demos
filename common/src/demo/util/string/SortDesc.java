package demo.util.string;

import java.util.Comparator;

public class SortDesc implements Comparator<String> {
	
	public static final SortDesc instance = new SortDesc();

	@Override
	public int compare(String a, String b) {
		return (-1) * a.compareTo(b);
	}
	
}
