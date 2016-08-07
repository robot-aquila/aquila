package ru.prolib.aquila.data.storage;

import ru.prolib.aquila.data.DataException;

public class DataStorageException extends DataException {
	private static final long serialVersionUID = 1L;
	
	public DataStorageException() {
		super();
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
