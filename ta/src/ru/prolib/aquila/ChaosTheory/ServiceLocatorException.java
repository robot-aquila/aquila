package ru.prolib.aquila.ChaosTheory;

public class ServiceLocatorException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ServiceLocatorException() {
		super();
	}
	
	public ServiceLocatorException(String msg) {
		super(msg);
	}
	
	public ServiceLocatorException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ServiceLocatorException(Throwable t) {
		super(t);
	}

}
