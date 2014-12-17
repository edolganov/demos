package demo.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureWrapper implements Future<Void>{
	
	final Future<?> real;

	public FutureWrapper(Future<?> real) {
		super();
		this.real = real;
	}


	@Override
	public boolean isDone() {
		return real.isDone();
	}
	

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return real.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return real.isCancelled();
	}

	@Override
	public Void get() throws InterruptedException, ExecutionException {
		real.get();
		return null;
	}

	@Override
	public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		real.get(timeout, unit);
		return null;
	}

}
