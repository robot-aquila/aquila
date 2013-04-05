package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * Геттер неисполненного кол-ва заявки из события {@link IBEventOrderStatus}.
 * <p>
 * 2012-12-18<br>
 * $Id: IBGetOrderRestQty.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderRestQty extends IBGetOrderStatusAttr<Long> {
	
	public IBGetOrderRestQty() {
		super();
	}

	@Override
	protected Long getEventAttr(IBEventOrderStatus event) {
		return (long) event.getRemaining();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderRestQty.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121219, 12311).toHashCode();
	}

}
