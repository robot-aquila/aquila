package ru.prolib.aquila.ta.ds;

public class MarketDataException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public MarketDataException(String msg) {
		super(msg);
	}
	
	public MarketDataException(String msg, Throwable t) {
		super(msg, t);
	}

}
