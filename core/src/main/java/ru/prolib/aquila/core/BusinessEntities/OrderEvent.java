package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;

/**
 * Событие заявки.
 * <p>
 * 2012-09-25<br>
 * $Id: OrderEvent.java 283 2012-09-26 17:01:17Z whirlwind $
 */
public class OrderEvent extends EventImpl {
	private final Order order;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param order заявка
	 */
	public OrderEvent(EventTypeSI type, Order order) {
		super(type);
		this.order = order;
	}
	
	/**
	 * Получить заявку.
	 * <p>
	 * @return заявка
	 */
	public Order getOrder() {
		return order;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == this.getClass() ) {
			OrderEvent o = (OrderEvent) other;
			return o.getType() == getType()
				&& o.getOrder() == getOrder();
		}
		return false;
	}

}
