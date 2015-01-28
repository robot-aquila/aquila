package ru.prolib.aquila.core.report.trades;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.TradeReport;

/**
 * Служебный интерфейс набора отчетов по трейдам.
 */
public interface EditableTradeReport extends TradeReport {
	
	/**
	 * Добавить сделку.
	 * <p>
	 * @param trade сделка
	 */
	public void addTrade(Trade trade);

}
