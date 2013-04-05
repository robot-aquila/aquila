package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;

/**
 * Интерфейс фабрики портфелей.
 * <p>
 * 2012-09-07<br>
 * $Id$
 */
public interface PortfolioFactory {

	/**
	 * Создать экземпляр портфеля.
	 * <p> 
	 * @param account счет портфеля
	 * @return экземпляр портфеля
	 */
	public EditablePortfolio createPortfolio(Account account);

}
