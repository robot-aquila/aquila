package ru.prolib.aquila.datatools;

public class IOException extends GeneralException {
	private static final long serialVersionUID = 1L;

	public IOException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public IOException(String msg) {
		this(msg, null);
	}
	
	public IOException(Throwable t) {
		this(null, t);
	}
	
	public IOException() {
		this(null, null);
	}
	
}
