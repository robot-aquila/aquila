package ru.prolib.aquila.data;

public class DataFormatException extends DataException {
	private static final long serialVersionUID = 1L;

	public DataFormatException(String msg) {
		super(msg);
	}
	
	public DataFormatException(Throwable t) {
		super(t);
	}
	
	public DataFormatException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataFormatException() {
		super();
	}

}
