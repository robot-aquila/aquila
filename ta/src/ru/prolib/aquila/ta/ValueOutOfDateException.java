package ru.prolib.aquila.ta;

public class ValueOutOfDateException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueOutOfDateException(String msg) {
		super(msg);
	}
		
	public ValueOutOfDateException(Throwable t) {
		super(t);
	}

	public ValueOutOfDateException() {
		super();
	}

}
