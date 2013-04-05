package ru.prolib.aquila.stat.counter;

public class CounterAlreadyExistsException extends CounterException {
	private static final long serialVersionUID = 1L;
	
	public CounterAlreadyExistsException(String id) {
		super("Counter already exists: " + id);
	}

}
