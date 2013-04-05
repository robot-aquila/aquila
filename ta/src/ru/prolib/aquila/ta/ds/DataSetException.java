package ru.prolib.aquila.ta.ds;

public class DataSetException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DataSetException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public DataSetException(String msg) {
		super(msg);
	}

}
