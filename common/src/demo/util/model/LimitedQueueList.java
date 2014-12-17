package demo.util.model;

import java.util.AbstractList;
import java.util.LinkedList;

public class LimitedQueueList<T> extends AbstractList<T>{
	
	private int limit = Integer.MAX_VALUE;
	private LinkedList<T> list = new LinkedList<T>();
	

	public LimitedQueueList(int limit) {
		super();
		setLimit(limit);
	}

	public LimitedQueueList() {
		super();
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		if(limit < 0){
			limit = 0;
		}
		this.limit = limit;
	}
	
	@Override
	public void add(int location, T object) {
		list.add(location, object);
		fixSize();
	}
	
	private void fixSize() {
		while(list.size() > limit){
			list.removeFirst();
		}
	}

	@Override
	public T set(int location, T object) {
		return list.set(location, object);
	}
	

	@Override
	public T get(int location) {
		return list.get(location);
	}

	@Override
	public int size() {
		return list.size();
	}
	

}
