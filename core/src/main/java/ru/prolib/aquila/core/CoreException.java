package ru.prolib.aquila.core;

public class CoreException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CoreException() {
		super();
	}
	
	public CoreException(String msg) {
		super(msg);
	}
	
	public CoreException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public CoreException(Throwable t) {
		super(t);
	}

}
