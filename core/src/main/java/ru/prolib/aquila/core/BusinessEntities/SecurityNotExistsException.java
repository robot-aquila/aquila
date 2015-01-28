package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение, выбрасываемое в случае, если указанный инструмент не найден.
 * <p>
 * 2012-05-30<br>
 * $Id: SecurityNotExistsException.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public class SecurityNotExistsException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Создать исключение
	 * <p>
	 * @param code код инструмента
	 */
	public SecurityNotExistsException(String code) {
		super("Security not exists: " + code);
	}
	
	public SecurityNotExistsException(SecurityDescriptor descr) {
		super("Security not exists: " + descr);
	}

}
