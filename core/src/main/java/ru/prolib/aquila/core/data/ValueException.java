package ru.prolib.aquila.core.data;

/**
 * Базовое исключение в связи со значением или его получением.
 */
public class ValueException extends DataException {
	private static final long serialVersionUID = 1L;
	
	public ValueException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ValueException(String msg) {
		super(msg);
	}
	
	public ValueException() {
		super();
	}
	
	public ValueException(Throwable t) {
		super(t);
	}

}
