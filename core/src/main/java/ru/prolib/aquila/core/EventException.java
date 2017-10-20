package ru.prolib.aquila.core;

public class EventException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public EventException() {
		super();
	}
	
	public EventException(String msg) {
		super(msg);
	}
	
	public EventException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public EventException(Throwable t) {
		super(t);
	}

}
