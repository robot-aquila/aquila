package ru.prolib.aquila.core.data;

public class ValueFormatException extends ValueException {
	private static final long serialVersionUID = 1L;
	
	public ValueFormatException(String valueId, String value) {
		super("[" + valueId + "] wrong format: " + value);
	}

}
