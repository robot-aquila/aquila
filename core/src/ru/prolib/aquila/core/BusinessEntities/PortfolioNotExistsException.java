package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение, выбрасываемое в случае, если указанный портфель не найден.
 * <p>
 * 2012-05-30<br>
 * $Id: PortfolioNotExistsException.java 264 2012-09-05 16:42:57Z whirlwind $
 */
public class PortfolioNotExistsException extends PortfolioException {
	private static final long serialVersionUID = 1L;
	
	public PortfolioNotExistsException(String code) {
		super("Portfolio not exists: " + code);
	}
	
	public PortfolioNotExistsException() {
		super();
	}

}
