package ru.prolib.aquila.quik.subsys;

/**
 * Сервис недоступен.
 * <p>
 * 2013-01-30<br>
 * $Id: QUIKServiceNotAvailableException.java 462 2013-01-30 17:37:31Z whirlwind $
 */
public class QUIKServiceNotAvailableException extends RuntimeException {
	private static final long serialVersionUID = -2018345230543299529L;
	
	public QUIKServiceNotAvailableException(String service) {
		super("Service not available: " + service);
	}

}
