package demo.util;

public class MemoryUtil {
	
	public static long getAllocatedMemory(){
		return Runtime.getRuntime().totalMemory();
	}
	
	public static double getAllocatedMemoryMB(){
		return ((double)getAllocatedMemory()) / 1024. / 1024.;
	}

}
