package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.Order;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;

/**
 * Геттер экземпляра заявки IB на основе события {@link IBEventOpenOrder}.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetOpenOrder.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOpenOrder implements G<Order> {
	
	public IBGetOpenOrder() {
		super();
	}

	@Override
	public Order get(Object source) {
		return source instanceof IBEventOpenOrder
			? ((IBEventOpenOrder) source).getOrder() : null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 134615).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetOpenOrder.class;
	}

}
