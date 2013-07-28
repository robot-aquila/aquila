package ru.prolib.aquila.core.report.trades;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderPool;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.report.TradeSelector;

/**
 * Селектор сделок на основании пула заявок.
 * <p>
 * Проверяет вхождение заявки, по которой была сделка, в пул заявок.
 */
public class OrderPoolTradeSelector implements TradeSelector {
	private final OrderPool orders;
	
	public OrderPoolTradeSelector(OrderPool orders) {
		super();
		this.orders = orders;
	}
	
	public OrderPool getOrderPool() {
		return orders;
	}

	@Override
	public boolean mustBeAdded(Trade trade, Order order) {
		return orders.isPooled(order);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if (other == null || other.getClass() != OrderPoolTradeSelector.class) {
			return false;
		}
		OrderPoolTradeSelector o = (OrderPoolTradeSelector) other;
		return new EqualsBuilder()
			.append(o.orders, orders)
			.isEquals();
	}

}
