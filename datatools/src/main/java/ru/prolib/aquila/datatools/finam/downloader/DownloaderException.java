package ru.prolib.aquila.datatools.finam.downloader;

import ru.prolib.aquila.datatools.finam.FinamException;

public class DownloaderException extends FinamException {
	private static final long serialVersionUID = 1L;
	
	public DownloaderException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DownloaderException(String msg) {
		this(msg, null);
	}
	
	public DownloaderException(Throwable t) {
		this(null, t);
	}
	
	public DownloaderException() {
		this(null, null);
	}

}
