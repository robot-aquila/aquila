package ru.prolib.aquila.core.data.row;

import ru.prolib.aquila.core.data.ValueException;

/**
 * Базовое исключение ряда.
 */
public class RowException extends ValueException {
	private static final long serialVersionUID = -7106285976794390088L;
	
	public RowException() {
		super();
	}
	
	public RowException(Throwable t) {
		super(t);
	}
	
	public RowException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public RowException(String msg) {
		super(msg);
	}

}
