package ru.prolib.aquila.ib.subsys.security;

/**
 * Общее исключение инструмента IB.
 * <p>
 * 2012-11-21<br>
 * $Id: IBSecurityException.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class IBSecurityException
	extends ru.prolib.aquila.core.BusinessEntities.SecurityException
{
	private static final long serialVersionUID = 7890337253153206762L;
	
	public IBSecurityException() {
		super();
	}
	
	public IBSecurityException(String msg) {
		super(msg);
	}
	
	public IBSecurityException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public IBSecurityException(Throwable t) {
		super(t);
	}

}
