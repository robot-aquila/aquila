package ru.prolib.aquila.ib.subsys.security;

/**
 * Прерывание во время операции ожидания.
 * <p> 
 * 2012-11-21<br>
 * $Id: IBSecurityInterruptedException.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityInterruptedException extends IBSecurityException {
	private static final long serialVersionUID = -892274853082132761L;
	
	public IBSecurityInterruptedException(Throwable t) {
		super(t);
	}

}
