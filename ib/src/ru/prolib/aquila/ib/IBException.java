package ru.prolib.aquila.ib;

/**
 * Базовое исключение IB.
 * <p>
 * 2012-11-26<br>
 * $Id: IBException.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class IBException extends Exception {
	private static final long serialVersionUID = -1238218816610354302L;
	
	public IBException(String msg) {
		super(msg);
	}
	
	public IBException(Throwable t) {
		super(t);
	}

	public IBException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public IBException() {
		super();
	}

}
