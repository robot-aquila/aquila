package ru.prolib.aquila.transaq.xml;

public class ParserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ParserException() {
		
	}
	
	public ParserException(Throwable t) {
		super(t);
	}
	
	public ParserException(String msg) {
		super(msg);
	}
	
	public ParserException(String msg, Throwable t) {
		super(msg, t);
	}

}
