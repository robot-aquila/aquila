package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение, выбрасываемое в случае, если инструмент с указанными кодом
 * инструмента и кодом класса уже присутствует в наборе.
 * <p>
 * 2012-07-04<br>
 * $Id: SecurityAlreadyExistsException.java 224 2012-07-08 06:04:52Z whirlwind $
 */
public class SecurityAlreadyExistsException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Создать исключение
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 */
	public SecurityAlreadyExistsException(String code, String classCode) {
		super("Security already exists: " + code + "@" + classCode);
	}
	
	/**
	 * Создать исключение
	 * <p>
	 * @param symbol дескриптор инструмента
	 */
	public SecurityAlreadyExistsException(Symbol symbol) {
		this(symbol.getCode(), symbol.getClassCode());
	}

}
