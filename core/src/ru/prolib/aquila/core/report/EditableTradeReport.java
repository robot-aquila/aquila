package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Сервисный интерфейс трейд-отчета.
 * <p>
 * Интерфейс предназначен для использования поставщиком данных.
 */
public interface EditableTradeReport extends TradeReport {
	
	/**
	 * Добавить сделку в отчет.
	 * <p>
	 * @param trade сделка
	 * @return null или новый отчет, если текущий был закрыт в результате сделки
	 */
	public EditableTradeReport addTrade(Trade trade);

}
