package demo.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DoneFuture<V> implements Future<V>{
	
	public static final DoneFuture<?> EMPTY_DONE_FUTURE = new DoneFuture<>();
	
	public V data;
	
	public DoneFuture() {
		this(null);
	}
	
	public DoneFuture(V data) {
		this.data = data;
	}


	@Override
	public boolean isDone() {
		return true;
	}
	

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return data;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return data;
	}

}
