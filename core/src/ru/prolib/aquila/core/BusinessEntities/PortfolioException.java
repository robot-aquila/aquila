package ru.prolib.aquila.core.BusinessEntities;

/**
 * Общее исключение портфеля.
 * <p>
 * 2012-05-30<br>
 * $Id: PortfolioException.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class PortfolioException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PortfolioException() {
		super();
	}
	
	public PortfolioException(String msg) {
		super(msg);
	}
	
	public PortfolioException(Throwable t) {
		super(t);
	}
	
	public PortfolioException(String msg, Throwable t) {
		super(msg, t);
	}

}
