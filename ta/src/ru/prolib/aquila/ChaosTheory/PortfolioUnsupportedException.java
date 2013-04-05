package ru.prolib.aquila.ChaosTheory;

/**
 * Операция не поддерживается.
 */
public class PortfolioUnsupportedException extends PortfolioException
{
	private static final long serialVersionUID = 1L;

	public PortfolioUnsupportedException(String msg) {
		super(msg);
	}
	
}