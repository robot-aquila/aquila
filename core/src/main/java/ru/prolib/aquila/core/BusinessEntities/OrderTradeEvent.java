package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие в связи с новой сделкой по заявке.
 */
public class OrderTradeEvent extends OrderEvent {
	private final Trade trade;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param order заявка
	 * @param trade сделка
	 */
	public OrderTradeEvent(EventType type, Order order, Trade trade) {
		super(type, order);
		this.trade = trade;
	}
	
	/**
	 * Получить сделку.
	 * <p>
	 * @return сделка
	 */
	public Trade getTrade() {
		return trade;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != OrderTradeEvent.class ) {
			return false;
		}
		OrderTradeEvent o = (OrderTradeEvent) other;
		return new EqualsBuilder()
			.append(getType(), o.getType())
			.append(getOrder(), o.getOrder())
			.append(trade, o.trade)
			.isEquals();
	}

}
