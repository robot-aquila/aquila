package ru.prolib.aquila.qforts.impl;

public class QFValidationException extends QFTransactionException {
	private static final long serialVersionUID = 1L;
	private final int code;
	
	public QFValidationException(String msg, Throwable t, int code) {
		super(msg, t);
		this.code = code;
	}
	
	public QFValidationException(String msg, int code) {
		super(msg);
		this.code = code;
	}
	
	public QFValidationException(Throwable t, int code) {
		super(t);
		this.code = code;
	}
	
	public QFValidationException(int code) {
		this.code = code;
	}
	
	public int getResultCode() {
		return code;
	}

}
