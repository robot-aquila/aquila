package ru.prolib.aquila.data.storage.file;

import ru.prolib.aquila.data.DataFormatException;

public class SimpleCsvL1FormatException extends DataFormatException {
	private static final long serialVersionUID = 1L;
	
	public SimpleCsvL1FormatException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SimpleCsvL1FormatException(String msg) {
		super(msg);
	}

}
