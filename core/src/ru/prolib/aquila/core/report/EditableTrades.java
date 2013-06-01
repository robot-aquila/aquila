package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Служебный интерфейс набора отчетов по трейдам.
 */
public interface EditableTrades extends Trades {
	
	/**
	 * Добавить сделку.
	 * <p>
	 * @param trade сделка
	 */
	public void addTrade(Trade trade);

}
