package demo.util.model;

public class Pair<A, B>{
	
	public A first;
	public B second;
	
	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	public Pair() {
		super();
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair [" + first + ", " + second + "]";
	}
	
	

}
