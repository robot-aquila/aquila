package ru.prolib.aquila.core.report.trades;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.report.TradeSelector;

/**
 * Заглушка селектора сделок.
 */
public class StubTradeSelector implements TradeSelector {
	
	public StubTradeSelector() {
		super();
	}

	/**
	 * Всегда возвращает true.
	 */
	@Override
	public boolean mustBeAdded(Trade trade, Order order) {
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == StubTradeSelector.class;
	}

}
