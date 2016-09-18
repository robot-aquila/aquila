package ru.prolib.aquila.data;

import ru.prolib.aquila.core.CoreException;

public class DataException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	public DataException(String msg) {
		super(msg);
	}
	
	public DataException(Throwable t) {
		super(t);
	}
	
	public DataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataException() {
		super();
	}

}
