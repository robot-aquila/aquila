package ru.prolib.aquila.core.data.row;

/**
 * Базовое исключение в связи с обращением к элементу ряда.
 */
public class RowValueException extends RowException {
	private static final long serialVersionUID = 8562948143536427454L;
	
	public RowValueException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public RowValueException(String msg) {
		super(msg);
	}
	
	public RowValueException() {
		super();
	}
	
	public RowValueException(Throwable t) {
		super(t);
	}

}
