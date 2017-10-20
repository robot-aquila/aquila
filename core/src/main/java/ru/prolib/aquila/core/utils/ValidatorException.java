package ru.prolib.aquila.core.utils;

/**
 * Общее исключение валидации.
 */
public class ValidatorException extends Exception {
	private static final long serialVersionUID = -418412294335935397L;
	
	public ValidatorException() {
		super();
	}
	
	public ValidatorException(String msg) {
		super(msg);
	}
	
	public ValidatorException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ValidatorException(Throwable t) {
		super(t);
	}

}
