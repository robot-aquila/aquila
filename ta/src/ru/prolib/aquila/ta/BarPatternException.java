package ru.prolib.aquila.ta;

public class BarPatternException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public BarPatternException() {
		super();
	}
	
	public BarPatternException(String msg) {
		super(msg);
	}
	
	public BarPatternException(Throwable t) {
		super(t);
	}
	
	public BarPatternException(String msg, Throwable t) {
		super(msg, t);
	}

}
