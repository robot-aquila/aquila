package ru.prolib.aquila.core.data;

/**
 * Common data provider exception.
 */
public class DataProviderException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DataProviderException(String msg) {
		super(msg);
	}
	
	public DataProviderException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataProviderException(Throwable t) {
		super(t);
	}
	
	public DataProviderException() {
		super();
	}

}
