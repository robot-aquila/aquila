package ru.prolib.aquila.core.data;


/**
 * Исключение, сигнализирующее о том, что значение неопределено или набор
 * значений не содержит затребованного значения.
 * 
 * 2012-04-17
 * $Id: ValueNotExistsException.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class ValueNotExistsException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueNotExistsException(String valueId) {
		super("Value not exists: " + valueId);
	}

	public ValueNotExistsException() {
		super();
	}

}
