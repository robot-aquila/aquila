package ru.prolib.aquila.quik.api;

/**
 * Базовое исключение фасада QUIK API.
 */
public class ApiServiceException extends Exception {
	private static final long serialVersionUID = -953710526163170143L;
	
	public ApiServiceException() {
		super();
	}
	
	public ApiServiceException(String msg) {
		super(msg);
	}
	
	public ApiServiceException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ApiServiceException(Throwable t) {
		super(t);
	}

}
