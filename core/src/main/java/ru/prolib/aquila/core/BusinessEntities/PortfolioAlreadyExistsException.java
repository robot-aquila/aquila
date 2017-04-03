package ru.prolib.aquila.core.BusinessEntities;

public class PortfolioAlreadyExistsException extends PortfolioException {
	private static final long serialVersionUID = 1L;
	
	public PortfolioAlreadyExistsException(String code) {
		super("Portfolio already exists: " + code);
	}

	public PortfolioAlreadyExistsException(Account account) {
		super("Portfolio already exists: " + account);
	}
	
	public PortfolioAlreadyExistsException() {
		super();
	}

}
