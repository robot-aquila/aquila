package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение выбрасываемое в случае попытки регистрации дублирующего портфеля.
 */
public class PortfolioAlreadyExistsException extends PortfolioException {
	private static final long serialVersionUID = -8464650230432428196L;
	
	public PortfolioAlreadyExistsException(Account account) {
		super("Portfolio already exists: " + account);
	}

}
