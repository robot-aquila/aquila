package ru.prolib.aquila.stat;

/**
 * 2012-02-02
 * $Id: TradeReportException.java 196 2012-02-02 20:24:38Z whirlwind $
 */
public class TradeReportException extends ReportException {
	private static final long serialVersionUID = 1L;

	public TradeReportException(String msg) {
		super(msg);
	}
	
	public TradeReportException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TradeReportException(Throwable t) {
		super(t);
	}
	
	public TradeReportException() {
		super();
	}

}
