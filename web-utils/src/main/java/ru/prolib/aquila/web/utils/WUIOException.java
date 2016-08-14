package ru.prolib.aquila.web.utils;

/**
 * IO exception of web-utils.
 * <p>
 * This class represents exceptions related to the Input/Output issues. It may
 * be wrapper of an {@link java.io.IOException IOException} or subsystem's own
 * exception.
 */
public class WUIOException extends WUException {
	private static final long serialVersionUID = 1L;

	public WUIOException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WUIOException(String msg) {
		super(msg);
	}
	
	public WUIOException(Throwable t) {
		super(t);
	}
	
	public WUIOException() {
		super();
	}

}
