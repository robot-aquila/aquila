package ru.prolib.aquila.data;

public class DataException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DataException(String msg) {
		super(msg);
	}
	
	public DataException(Throwable t) {
		super(t);
	}
	
	public DataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataException() {
		super();
	}

}
