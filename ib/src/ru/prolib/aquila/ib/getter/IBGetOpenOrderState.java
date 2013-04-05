package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.OrderState;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;

/**
 * Геттер состояния заявки IB на основе события {@link IBEventOpenOrder}.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetOpenOrderState.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOpenOrderState implements G<OrderState> {
	
	public IBGetOpenOrderState() {
		super();
	}

	@Override
	public OrderState get(Object source) {
		return source instanceof IBEventOpenOrder
			? ((IBEventOpenOrder) source).getOrderState() : null;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetOpenOrderState.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121217, 173323).toHashCode();
	}

}
