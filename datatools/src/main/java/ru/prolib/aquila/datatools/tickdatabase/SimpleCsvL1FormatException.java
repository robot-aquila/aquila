package ru.prolib.aquila.datatools.tickdatabase;

import ru.prolib.aquila.core.CoreException;

public class SimpleCsvL1FormatException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	public SimpleCsvL1FormatException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SimpleCsvL1FormatException(String msg) {
		super(msg);
	}

}
