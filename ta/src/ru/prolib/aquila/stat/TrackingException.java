package ru.prolib.aquila.stat;

public class TrackingException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TrackingException(String msg) {
		super(msg);
	}
	
	public TrackingException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TrackingException(Throwable t) {
		super(t);
	}
	
	public TrackingException() {
		super();
	}

}
