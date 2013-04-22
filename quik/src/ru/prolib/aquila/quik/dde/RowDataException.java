package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.data.ValueException;

/**
 * Базовое исключение при работе с данными ряда.
 */
public class RowDataException extends ValueException {
	private static final long serialVersionUID = -3120575849169688559L;
	
	public RowDataException() {
		super();
	}
	
	public RowDataException(String msg) {
		super(msg);
	}
	
	public RowDataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public RowDataException(Throwable t) {
		super(t);
	}

}
