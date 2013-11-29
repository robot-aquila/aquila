package ru.prolib.aquila.probe;

public class HistoricalDataException extends Exception {
	private static final long serialVersionUID = -5228083591802004334L;
	
	public HistoricalDataException() {
		super();
	}
	
	public HistoricalDataException(String msg) {
		super(msg);
	}
	
	public HistoricalDataException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public HistoricalDataException(Throwable t) {
		super(t);
	}

}
