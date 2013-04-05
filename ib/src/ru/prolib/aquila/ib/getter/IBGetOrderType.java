package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;


import com.ib.client.Order;

/**
 * Геттер типа заявки на основе заявки IB.
 * <p>
 * 2012-12-16<br>
 * $Id: IBGetOrderType.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderType extends IBGetOrderAttr<String> {
	
	public IBGetOrderType() {
		super();
	}

	@Override
	protected String getOrderAttr(Order order) {
		return order.m_orderType;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderType.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121217, 5425).toHashCode();
	}

}
