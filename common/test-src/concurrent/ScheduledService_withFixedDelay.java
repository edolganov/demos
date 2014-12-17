package concurrent;


import demo.util.concurrent.ExecutorsUtil;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



/**
 * http://stackoverflow.com/questions/2082304/what-causes-scheduled-threads-not-to-run-in-java
 * http://stackoverflow.com/questions/6844575/scheduleatfixedrate-slow-late
 */
public class ScheduledService_withFixedDelay {
	
	public static void main(String[] args) throws IOException {
		
		ScheduledExecutorService pool = ExecutorsUtil.newScheduledThreadPool("test", 2);
		pool.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("1000");
				try {
					Thread.sleep(1000);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}, 100, 1000, TimeUnit.MILLISECONDS);
		pool.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("500");
				try {
					Thread.sleep(500);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}, 100, 500, TimeUnit.MILLISECONDS);
		pool.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("100");
				try {
					Thread.sleep(100);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}, 100, 100, TimeUnit.MILLISECONDS);
		
		System.in.read();
	}

}
