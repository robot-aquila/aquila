package ru.prolib.aquila.web.utils;

public class WUInvalidResponseException extends WUProtocolException {
	private static final long serialVersionUID = 1L;

	public WUInvalidResponseException(String msg) {
		super(msg);
	}

}
