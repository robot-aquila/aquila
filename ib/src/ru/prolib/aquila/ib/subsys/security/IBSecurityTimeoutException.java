package ru.prolib.aquila.ib.subsys.security;

/**
 * Таймаут во время ожидания выполнения операции.
 * <p>
 * 2012-11-21<br>
 * $Id: IBSecurityTimeoutException.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityTimeoutException extends IBSecurityException {
	private static final long serialVersionUID = 907692306834562513L;
	
	/**
	 * Создать исключение.
	 * <p>
	 * @param timeout таймаут милисекунд
	 */
	public IBSecurityTimeoutException(long timeout) {
		super("Timeout: " + timeout + " ms.");
	}

}
