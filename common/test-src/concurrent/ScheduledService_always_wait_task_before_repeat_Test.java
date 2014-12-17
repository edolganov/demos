package concurrent;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Ignore;

@Ignore
public class ScheduledService_always_wait_task_before_repeat_Test {
	
	static int count = 10;
	static Object monitor = new Object();
	
	public static void main(String[] args) throws Exception {

		ScheduledExecutorService readers = Executors.newScheduledThreadPool(3);
		readers.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				
				System.out.println(Thread.currentThread().getName());
				downCount();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
				
			}
		}, 0, 10, MILLISECONDS);
		
		
		synchronized (monitor) {
			if(count > 0) monitor.wait();
			readers.shutdown();
		}
		
	}
	
	static synchronized void downCount(){
		count--;
		if(count < 1){
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
	}

}
