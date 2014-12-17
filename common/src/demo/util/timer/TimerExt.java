package demo.util.timer;

import static demo.util.ExceptionUtil.*;
import static demo.util.Util.*;
import demo.util.model.CallableVoid;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;

public class TimerExt extends Timer {
	
	private Log log = getLog(getClass());
	private String name;

	public TimerExt() {
		super();
	}

	public TimerExt(boolean isDaemon) {
		super(isDaemon);
	}

	public TimerExt(String name, boolean isDaemon) {
		super(name, isDaemon);
		this.name = name;
	}

	public TimerExt(String name) {
		super(name);
		this.name = name;
	}
	
	
	
	
	public void trySchedule(CallableVoid body, long delay) {
		super.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					body.call();
				}catch(Throwable t){
					log.error(getLogPrefix() + "can't schedule: "+t);
				}
			}
		}, delay);
		
		log.info(getLogPrefix() + "timer started: delay="+delay);
	}
	
	
	public void schedule(CallableVoid body, long delay) {
		super.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					body.call();
				}catch(Exception e){
					throw getRuntimeExceptionOrThrowError(e);
				}
			}
		}, delay);
		
		log.info(getLogPrefix() + "timer started: delay="+delay);
	}
	
	public void tryScheduleAtFixedRate(CallableVoid body, long delay, long period) {
		
		super.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				try {
					body.call();
				}catch(Throwable t){
					log.error(getLogPrefix() + "can't schedule: "+t);
				}
			}
		}, delay, period);
		
		log.info(getLogPrefix() + "timer started: delay="+delay+", period="+period);
	}
	
	
	public void scheduleAtFixedRate(CallableVoid body, long delay, long period) {
		
		super.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				try {
					body.call();
				}catch(Exception e){
					throw getRuntimeExceptionOrThrowError(e);
				}
			}
		}, delay, period);
		
		log.info(getLogPrefix() + "timer started: delay="+delay+", period="+period);
	}

	private String getLogPrefix() {
		return name != null? name + ": " : "";
	}
	
	

}
