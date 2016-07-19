package ru.prolib.aquila.finam.tools.web;

public class DataExportFormException extends DataExportException {
	private static final long serialVersionUID = 1L;
	
	public DataExportFormException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DataExportFormException(String msg) {
		this(msg, null);
	}
	
	public DataExportFormException(Throwable t) {
		this(null, t);
	}
	
	public DataExportFormException() {
		this(null, null);
	}

}
