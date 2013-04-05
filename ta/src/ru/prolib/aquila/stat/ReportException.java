package ru.prolib.aquila.stat;

public class ReportException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ReportException(String msg) {
		super(msg);
	}
	
	public ReportException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ReportException(Throwable t) {
		super(t);
	}
	
	public ReportException() {
		super();
	}

}
