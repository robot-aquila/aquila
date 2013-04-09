package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.T2QOrder;

/**
 * Событие о поступлении новой или об изменении состояния заявки.
 */
public class OrderEvent extends EventImpl {
	private final T2QOrder order;

	public OrderEvent(EventType type, T2QOrder order) {
		super(type);
		this.order = order;
	}
	
	/**
	 * Получить состояние заявки.
	 * <p>
	 * @return состояние заявки
	 */
	public T2QOrder getOrderState() {
		return order;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == OrderEvent.class ) {
			OrderEvent o = (OrderEvent) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(order, o.order)
				.isEquals();
		} else {
			return false;
		}
	}

}
