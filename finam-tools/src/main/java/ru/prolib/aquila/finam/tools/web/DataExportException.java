package ru.prolib.aquila.finam.tools.web;

public class DataExportException extends Exception {
	private static final long serialVersionUID = 1L;
	private final ErrorClass errorClass;
	
	public DataExportException(ErrorClass errorClass, String msg, Throwable t) {
		super(msg, t);
		this.errorClass = errorClass;
	}
	
	public DataExportException(ErrorClass errorClass, String msg) {
		this(errorClass, msg, null);
	}
	
	public DataExportException(ErrorClass errorClass, Throwable t) {
		this(errorClass, null, t);
	}
	
	public DataExportException(ErrorClass errorClass) {
		this(errorClass, null, null);
	}
	
	public ErrorClass getErrorClass() {
		return errorClass;
	}

}
