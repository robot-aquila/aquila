package ru.prolib.aquila.datatools.finam;

public class FinamException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public FinamException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public FinamException(String msg) {
		this(msg, null);
	}
	
	public FinamException(Throwable t) {
		this(null, t);
	}
	
	public FinamException() {
		this(null, null);
	}
	
}
