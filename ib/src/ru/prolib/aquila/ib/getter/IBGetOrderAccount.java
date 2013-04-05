package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;


import com.ib.client.Order;

/**
 * Геттер кода счета на основе заявки IB.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetOrderAccount.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderAccount extends IBGetOrderAttr<String> {
	
	public IBGetOrderAccount() {
		super();
	}

	@Override
	protected String getOrderAttr(Order order) {
		return order.m_account;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 122703)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderAccount.class;
	}

}
