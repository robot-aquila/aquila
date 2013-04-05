package ru.prolib.aquila.ChaosTheory;

/**
 * Общее исключение портфеля.
 */
public class PortfolioException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PortfolioException() {
		super();
	}
	
	public PortfolioException(String msg) {
		super(msg);
	}
	
	public PortfolioException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public PortfolioException(Throwable t) {
		super(t);
	}
	
}