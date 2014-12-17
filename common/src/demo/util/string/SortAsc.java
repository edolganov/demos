package demo.util.string;

import java.util.Comparator;

public class SortAsc implements Comparator<String> {
	
	public static final SortAsc instance = new SortAsc();

	@Override
	public int compare(String a, String b) {
		return a.compareTo(b);
	}
	
}
