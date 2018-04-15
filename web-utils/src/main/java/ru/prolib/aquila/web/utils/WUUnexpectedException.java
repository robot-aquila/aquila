package ru.prolib.aquila.web.utils;

/**
 * This class of error indicates that something wrong is happen
 * what shouldn't be happen normally. This class is used to route
 * up unexpected exceptions occurred on lower levels.
 */
public class WUUnexpectedException extends WUException {
	private static final long serialVersionUID = 1L;

	public WUUnexpectedException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WUUnexpectedException(String msg) {
		super(msg);
	}
	
	public WUUnexpectedException(Throwable t) {
		super(t);
	}
	
	public WUUnexpectedException() {
		super();
	}
	
}
