package demo.util.model;

import static demo.util.StringUtil.*;
import static demo.util.Util.*;

import java.util.List;

public class HoursAndMinutes implements Comparable<HoursAndMinutes>{
	
	private int hours;
	private int minutes;
	
	public HoursAndMinutes(int hours, int minutes) {
		super();
		this.hours = hours;
		this.minutes = minutes;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}
	
	public String getTime(){
		return zeroPrefix(hours)+":"+zeroPrefix(minutes);
	}
	
	private static String zeroPrefix(int num){
		return (num >= 0 && num < 10) ? "0" + num : ""+ num;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hours;
		result = prime * result + minutes;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoursAndMinutes other = (HoursAndMinutes) obj;
		if (hours != other.hours)
			return false;
		if (minutes != other.minutes)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getTime();
	}
	
	@Override
	public int compareTo(HoursAndMinutes o) {
		int a = hours * 60 + minutes;
		int b = o.hours * 60 + minutes;
		return Integer.compare(a, b);
	}

	public static HoursAndMinutes tryParseHHmm(String val, HoursAndMinutes defVal) {
		if( ! hasText(val)) return defVal;
		
		List<String> items = strToList(val, ":");
		if(items.size() < 2) return defVal;
		
		int hours = tryParseInt(items.get(0), -1);
		int mins = tryParseInt(items.get(1), -1);
		if(
			hours < 0 
			|| hours > 23 
			|| mins < 0 
			|| mins > 59
				) return defVal;
		
		return new HoursAndMinutes(hours, mins);
	}


	
	
	
	
	
	
}