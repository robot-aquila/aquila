package ru.prolib.aquila.ta.ds;

public class BarWriterException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public BarWriterException(String msg) {
		super(msg);
	}
	
	public BarWriterException(String msg, Throwable t) {
		super(msg, t);
	}

}
