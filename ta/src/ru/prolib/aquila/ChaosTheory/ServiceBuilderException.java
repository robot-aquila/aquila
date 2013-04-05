package ru.prolib.aquila.ChaosTheory;

public class ServiceBuilderException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ServiceBuilderException() {
		super();
	}
	
	public ServiceBuilderException(String msg) {
		super(msg);
	}
	
	public ServiceBuilderException(Throwable t) {
		super(t);
	}
	
	public ServiceBuilderException(String msg, Throwable t) {
		super(msg, t);
	}

}
