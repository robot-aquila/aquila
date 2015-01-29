package ru.prolib.aquila.ui;

@Deprecated
public class DriverException extends RuntimeException {

	/**
	 * $Id: DriverException.java 554 2013-03-01 13:43:04Z whirlwind $
	 */
	private static final long serialVersionUID = -2721713676378207212L;

	public DriverException(String msg) {
		super(msg);
	}
	
	public DriverException(Throwable t) {
		super(t);
	}

	public DriverException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DriverException() {
		super();
	}

}
