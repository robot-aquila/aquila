package ru.prolib.aquila.web.utils;

import ru.prolib.aquila.data.DataException;

/**
 * Common exception of web-utils.
 * <p>
 * This class is a base class for all package exceptions.
 */
public class WUException extends DataException {
	private static final long serialVersionUID = 1L;
	
	public WUException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WUException(String msg) {
		super(msg);
	}
	
	public WUException(Throwable t) {
		super(t);
	}
	
	public WUException() {
		super();
	}

}
