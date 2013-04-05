package ru.prolib.aquila.ta.ds;

public class DealWriterException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DealWriterException(String msg) {
		super(msg);
	}
	
	public DealWriterException(String msg, Throwable t) {
		super(msg, t);
	}

}
