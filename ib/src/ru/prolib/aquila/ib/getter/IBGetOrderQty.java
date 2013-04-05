package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;


import com.ib.client.Order;

/**
 * Геттер количества заявки на основе заявки IB.
 * <p>
 * 2012-12-16<br>
 * $Id: IBGetOrderQty.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderQty extends IBGetOrderAttr<Long> {
	
	public IBGetOrderQty() {
		super();
	}

	@Override
	protected Long getOrderAttr(Order order) {
		return (long) order.m_totalQuantity;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121217, 13959).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderQty.class;
	}

}
