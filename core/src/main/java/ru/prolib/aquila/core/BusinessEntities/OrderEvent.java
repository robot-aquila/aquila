package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl.ContainerEventImpl;

/**
 * Событие заявки.
 * <p>
 * 2012-09-25<br>
 * $Id: OrderEvent.java 283 2012-09-26 17:01:17Z whirlwind $
 */
public class OrderEvent extends ContainerEventImpl {
	private final Order order;

	/**
	 * Создать событие.
	 * <p>
	 * @param type тип события
	 * @param order заявка
	 */
	public OrderEvent(EventType type, Order order) {
		super(type, order);
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
	
	@Override
	public String toString() {
		return super.toString() + "[orderID=" + order.getID() + "]";
	}

}
