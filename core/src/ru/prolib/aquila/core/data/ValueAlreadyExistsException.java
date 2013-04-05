package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.data.ValueException;

/**
 * Затребованное значение не найдено или не существует.
 * 
 * 2012-04-19
 * $Id: ValueAlreadyExistsException.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class ValueAlreadyExistsException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueAlreadyExistsException(String valueId) {
		super("Value already exists: " + valueId);
	}

	public ValueAlreadyExistsException() {
		super();
	}
	
}
