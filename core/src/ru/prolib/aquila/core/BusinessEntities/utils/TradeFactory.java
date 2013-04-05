package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * Интерфейс фабрики сделок.
 * <p>
 * 2012-11-04<br>
 * $Id: TradeFactory.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public interface TradeFactory {
	
	/**
	 * Создать экземпляр сделки.
	 * <p>
	 * @return экземпляр сделки
	 */
	public Trade createTrade();

}
