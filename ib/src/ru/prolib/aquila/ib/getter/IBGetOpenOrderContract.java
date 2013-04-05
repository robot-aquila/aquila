package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.Contract;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;

/**
 * Геттер экземпляра контракта IB на основе события {@link IBEventOpenOrder}.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetOpenOrderContract.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOpenOrderContract implements G<Contract> {
	
	public IBGetOpenOrderContract() {
		super();
	}

	@Override
	public Contract get(Object source) {
		return source instanceof IBEventOpenOrder
			? ((IBEventOpenOrder) source).getContract() : null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 163533).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetOpenOrderContract.class;
	}

}
