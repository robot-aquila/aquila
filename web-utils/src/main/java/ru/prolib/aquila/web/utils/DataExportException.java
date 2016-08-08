package ru.prolib.aquila.web.utils;

import ru.prolib.aquila.data.DataException;

public class DataExportException extends DataException {
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
