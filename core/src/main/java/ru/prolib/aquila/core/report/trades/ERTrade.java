package ru.prolib.aquila.core.report.trades;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.RTrade;

/**
 * Сервисный интерфейс записи трейд-отчета.
 * <p>
 * Интерфейс предназначен для использования поставщиком данных.
 */
public interface ERTrade extends RTrade {
	
	/**
	 * Добавить сделку в отчет.
	 * <p>
	 * @param trade сделка
	 * @return null или новый отчет, если текущий был закрыт в результате сделки
	 */
	public ERTrade addTrade(Trade trade);

}
