package ru.prolib.aquila.finamtools;

public class FinamDownloaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public FinamDownloaderException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public FinamDownloaderException(String msg) {
		this(msg, null);
	}
	
	public FinamDownloaderException(Throwable t) {
		this(null, t);
	}
	
	public FinamDownloaderException() {
		this(null, null);
	}

}
