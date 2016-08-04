package ru.prolib.aquila.web.utils;

public class InvalidResponseException extends DataExportException {
	private static final long serialVersionUID = 1L;

	public InvalidResponseException(String msg) {
		super(ErrorClass.RESPONSE_VALIDATION, msg);
	}

}
