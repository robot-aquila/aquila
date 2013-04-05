package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;


import com.ib.client.Order;

/**
 * Геттер направления заявки на основе заявки IB.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetOrderDir.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderDir extends IBGetOrderAttr<String> {
	
	public IBGetOrderDir() {
		super();
	}

	@Override
	protected String getOrderAttr(Order order) {
		return order.m_action;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderDir.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 200323).toHashCode();
	}

}
