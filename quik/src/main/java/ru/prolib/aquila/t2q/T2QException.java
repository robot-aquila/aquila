package ru.prolib.aquila.t2q;

/**
 * Базовой исключение подсистемы T2Q.
 * <p>
 * 2013-02-06<br>
 * $Id$
 */
public class T2QException extends Exception {
	private static final long serialVersionUID = -6136900083872553544L;
	
	public T2QException() {
		super();
	}
	
	public T2QException(Throwable t) {
		super(t);
	}
	
	public T2QException(String msg) {
		super(msg);
	}
	
	public T2QException(String msg, Throwable t) {
		super(msg, t);
	}

}
