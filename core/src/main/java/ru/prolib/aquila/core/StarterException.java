package ru.prolib.aquila.core;

/**
 * Базовое исключение стартера.
 * <p>
 * 2012-11-24<br>
 * $Id: StarterException.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class StarterException extends RuntimeException {
	private static final long serialVersionUID = -4770860811722249219L;
	
	public StarterException(String msg) {
		super(msg);
	}
	
	public StarterException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public StarterException(Throwable t) {
		super(t);
	}
	
	public StarterException() {
		super();
	}

}
