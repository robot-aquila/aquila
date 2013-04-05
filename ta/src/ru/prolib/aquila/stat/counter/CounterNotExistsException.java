package ru.prolib.aquila.stat.counter;

public class CounterNotExistsException extends CounterException {
	private static final long serialVersionUID = 1L;
	
	public CounterNotExistsException(String id) {
		super("Counter not exists: " + id);
	}

}
