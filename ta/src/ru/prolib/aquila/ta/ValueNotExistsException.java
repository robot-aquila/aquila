package ru.prolib.aquila.ta;

public class ValueNotExistsException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueNotExistsException(String valueId) {
		super("Value not exists: " + valueId);
	}

	public ValueNotExistsException() {
		super();
	}

}
