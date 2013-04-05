package ru.prolib.aquila.ta;

public class ValueExistsException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueExistsException(String valueId) {
		super("Value already exists: " + valueId);
	}

}
