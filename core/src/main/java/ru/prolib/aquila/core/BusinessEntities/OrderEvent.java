package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;

/**
 * Событие заявки.
 * <p>
 * 2012-09-25<br>
 * $Id: OrderEvent.java 283 2012-09-26 17:01:17Z whirlwind $
 */
public class OrderEvent extends OSCEventImpl {
	private final Order order;

	/**
	 * Constructor.
	 * <p>
	 * @param type - type of event
	 * @param order - the order
	 * @param time - time of event
	 */
	public OrderEvent(EventType type, Order order, Instant time) {
		super(type, order, time);
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
				&& o.getOrder() == getOrder()
				&& new EqualsBuilder()
					.append(o.getUpdatedTokens(), getUpdatedTokens())
					.append(o.getTime(), getTime())
					.isEquals();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[orderID=" + order.getID() + "]";
	}

}
