package ru.prolib.aquila.stat.counter;

public class CounterDataUnavailableException extends CounterException {
	private static final long serialVersionUID = 1L;
	
	public CounterDataUnavailableException() {
		super();
	}
	
	public CounterDataUnavailableException(String msg) {
		super("Counter data unavailable: " + msg);
	}

}
