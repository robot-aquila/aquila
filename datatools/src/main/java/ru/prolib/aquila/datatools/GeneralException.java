package ru.prolib.aquila.datatools;

public class GeneralException extends Exception {
	private static final long serialVersionUID = 1L;

	public GeneralException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public GeneralException(String msg) {
		this(msg, null);
	}
	
	public GeneralException(Throwable t) {
		this(null, t);
	}
	
	public GeneralException() {
		this(null, null);
	}
	
}
