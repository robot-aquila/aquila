package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Интерфейс селектора сделок.
 * <p>
 * Селектор сделок позволяет определять логику отбора сделок при добавлении
 * их в трейд-репорт. При этом механизм формирования отчета остается неизменным
 * для любых аналитических разрезов. 
 */
public interface TradeSelector {
	
	/**
	 * Проверить сделку на необходимость добавления в трейд-репорт.
	 * <p>
	 * @param trade сделка
	 * @param order заявка, по которой исполнена сделка
	 * @return true - добавить сделку, false - игнорировать сделку
	 */
	public boolean mustBeAdded(Trade trade, Order order);

}
