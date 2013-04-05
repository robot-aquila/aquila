package ru.prolib.aquila.stat.counter;

public class CounterException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CounterException(String msg) {
		super(msg);
	}
	
	public CounterException(Throwable t) {
		super(t);
	}
	
	public CounterException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public CounterException() {
		super();
	}

}
