package demo.util;

public class MemoryExtUtil {
	
	private static long fSLEEP_INTERVAL = 100;
	
	public static long getMemoryUse(){
	    putOutTheGarbage();
	    long totalMemory = Runtime.getRuntime().totalMemory();

	    putOutTheGarbage();
	    long freeMemory = Runtime.getRuntime().freeMemory();

	    return (totalMemory - freeMemory);
	}
	
	public static int getApproximateSize_Mb(long startMemoryUse, long endMemoryUse){
		return (int)((endMemoryUse - startMemoryUse) / 1024L / 1024L);
	}
  
	private static void putOutTheGarbage() {
	    collectGarbage();
	    collectGarbage();
	 }
	


	private static void collectGarbage() {
		try {
			System.gc();
			Thread.sleep(fSLEEP_INTERVAL);
			System.runFinalization();
			Thread.sleep(fSLEEP_INTERVAL);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
