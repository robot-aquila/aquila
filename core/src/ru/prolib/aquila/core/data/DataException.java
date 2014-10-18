package ru.prolib.aquila.core.data;

public class DataException extends Exception {
	private static final long serialVersionUID = -7970612565474293076L;

	public DataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataException(String msg) {
		super(msg);
	}
	
	public DataException() {
		super();
	}
	
	public DataException(Throwable t) {
		super(t);
	}
}
