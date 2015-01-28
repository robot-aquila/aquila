package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение, выбрасываемое в случае, если указанному коду инструмента
 * соответствуют более одного инструмента с различными кодами класса.
 * <p>
 * 2012-06-09<br>
 * $Id: SecurityAmbiguousException.java 223 2012-07-04 12:26:58Z whirlwind $
 */
public class SecurityAmbiguousException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Создать инсключение
	 * <p>
	 * @param code код инструмента
	 */
	public SecurityAmbiguousException(String code) {
		super("Ambiguous security code: " + code);
	}

}
