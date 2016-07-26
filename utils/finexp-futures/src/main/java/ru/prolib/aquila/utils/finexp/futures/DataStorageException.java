package ru.prolib.aquila.utils.finexp.futures;

public class DataStorageException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DataStorageException() {
		
	}
	
	public DataStorageException(String msg) {
		super(msg);
	}
	
	public DataStorageException(Throwable t) {
		super(t);
	}
	
	public DataStorageException(String msg, Throwable t) {
		super(msg, t);
	}

}
