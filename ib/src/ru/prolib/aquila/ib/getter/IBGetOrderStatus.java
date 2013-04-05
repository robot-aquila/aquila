package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * Геттер строки статуса из события {@link IBEventOrderStatus}.
 * <p>
 * 2012-12-18<br>
 * $Id: IBGetOrderStatus.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderStatus extends IBGetOrderStatusAttr<String> {
	
	public IBGetOrderStatus() {
		super();
	}

	@Override
	protected String getEventAttr(IBEventOrderStatus event) {
		return event.getStatus();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderStatus.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121219, 11059).toHashCode();
	}

}
