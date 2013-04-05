package ru.prolib.aquila.ta;

@Deprecated
public class ValueException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ValueException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ValueException(String msg) {
		super(msg);
	}
	
	public ValueException() {
		super();
	}
	
	public ValueException(Throwable t) {
		super(t);
	}

}
