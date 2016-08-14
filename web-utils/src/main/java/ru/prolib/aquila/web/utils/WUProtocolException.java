package ru.prolib.aquila.web.utils;

/**
 * Protocol exception of web-utils.
 * <p>
 * This class of exception points to a higher level of an IO error. Usually it
 * is related to the application layer protocols like HTTP protocol errors.
 * Nevertheless, this class inherited common IO-exception and can be caught as
 * {@link WUIOException}.
 */
public class WUProtocolException extends WUIOException {
	private static final long serialVersionUID = 1L;

	public WUProtocolException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WUProtocolException(String msg) {
		super(msg);
	}
	
	public WUProtocolException(Throwable t) {
		super(t);
	}
	
	public WUProtocolException() {
		super();
	}

}
