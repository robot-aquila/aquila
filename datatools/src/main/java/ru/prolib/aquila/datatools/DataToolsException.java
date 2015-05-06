package ru.prolib.aquila.datatools;

public class DataToolsException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataToolsException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataToolsException(String msg) {
		this(msg, null);
	}
	
	public DataToolsException(Throwable t) {
		this(null, t);
	}
	
	public DataToolsException() {
		this(null, null);
	}
	
}
