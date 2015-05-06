package ru.prolib.aquila.datatools.finam.downloader;

public class WebFormException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public WebFormException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WebFormException(String msg) {
		this(msg, null);
	}
	
	public WebFormException(Throwable t) {
		this(null, t);
	}
	
	public WebFormException() {
		this(null, null);
	}

}
