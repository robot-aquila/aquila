package ru.prolib.aquila.core.data.row;

/**
 * Базовое исключение набора рядов.
 */
public class RowSetException extends RowException {
	private static final long serialVersionUID = 5039954962148774549L;
	
	public RowSetException() {
		super();
	}
	
	public RowSetException(Throwable t) {
		super(t);
	}
	
	public RowSetException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public RowSetException(String msg) {
		super(msg);
	}

}
