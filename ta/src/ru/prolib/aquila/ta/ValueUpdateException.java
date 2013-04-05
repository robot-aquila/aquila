package ru.prolib.aquila.ta;

public class ValueUpdateException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueUpdateException(Throwable t) {
		super("Update error: " + t.getMessage(), t);
	}

	public ValueUpdateException(String msg) {
		super(msg);
	}

}
