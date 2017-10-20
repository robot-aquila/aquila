package ru.prolib.aquila.core;

/**
 * Исключение-обертка {@link java.lang.InterruptedException}.
 * <p>
 * 2012-11-24<br>
 * $Id: StarterInterruptedException.java 322 2012-11-24 12:59:46Z whirlwind $
 */
public class StarterInterruptedException extends StarterException {
	private static final long serialVersionUID = 7518787621006567327L;
	
	public StarterInterruptedException(InterruptedException e) {
		super(e);
	}

}
