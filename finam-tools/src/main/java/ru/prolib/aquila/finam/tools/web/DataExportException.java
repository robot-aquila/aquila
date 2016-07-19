package ru.prolib.aquila.finam.tools.web;

public class DataExportException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public DataExportException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataExportException(String msg) {
		this(msg, null);
	}
	
	public DataExportException(Throwable t) {
		this(null, t);
	}
	
	public DataExportException() {
		this(null, null);
	}

}
