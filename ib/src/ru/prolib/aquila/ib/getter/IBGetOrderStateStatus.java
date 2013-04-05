package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.OrderState;

import ru.prolib.aquila.core.data.G;

/**
 * Геттер направления заявки на объекта состояния заявки IB.
 * <p>
 * 2012-12-16<br>
 * $Id: IBGetOrderStateStatus.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderStateStatus implements G<String> {
	
	public IBGetOrderStateStatus() {
		super();
	}

	@Override
	public String get(Object source) {
		if ( source instanceof OrderState ) {
			return ((OrderState) source).m_status;
		} else {
			return null;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121217, 20359).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetOrderStateStatus.class;
	}

}
