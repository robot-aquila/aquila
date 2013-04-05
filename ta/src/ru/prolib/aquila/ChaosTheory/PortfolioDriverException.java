package ru.prolib.aquila.ChaosTheory;

public class PortfolioDriverException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PortfolioDriverException(String msg) {
		super(msg);
	}
	
	public PortfolioDriverException(Throwable t) {
		super(t);
	}
	
	public PortfolioDriverException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public PortfolioDriverException() {
		super();
	}
	
}