package ru.prolib.aquila.core.sm;

/**
 * Базовое исключение автомата.
 */
public class SMException extends Exception {
	private static final long serialVersionUID = 2357457519668890717L;
	
	public SMException() {
		super();
	}
	
	public SMException(String msg) {
		super(msg);
	}
	
	public SMException(Throwable t) {
		super(t);
	}
	
	public SMException(String msg, Throwable t) {
		super(msg, t);
	}

}
