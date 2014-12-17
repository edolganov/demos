package demo.util.async;


import demo.util.model.Shutdownable;

import java.util.Timer;
import java.util.TimerTask;





public class SingleTimerAction implements Shutdownable {
	
    private static long timerId;

    private synchronized static long nextId() {
        return timerId++;
    }
	
	private volatile String saveReqId;
	private Timer timer = new Timer("SingleTimerAction-"+nextId());
	private long delay;
	
	public SingleTimerAction(long delay) {
		super();
		this.delay = delay;
	}



	public void doSingleAction(final Runnable r){
		
		final String curReqId = System.currentTimeMillis()+"-"+System.nanoTime();
		saveReqId = curReqId;
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				//check for valid req
				String saveReqId = SingleTimerAction.this.saveReqId;
				if( curReqId != saveReqId){
					return;
				}
				
				//action
				r.run();
			}
			
		}, delay);
	}
	
	@Override
	public void shutdown(){
		timer.cancel();
	}

}
