package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * Геттер исполненного объема заявки из события {@link IBEventOrderStatus}.
 * <p>
 * 2012-12-25<br>
 * $Id: IBGetOrderExecVolume.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderExecVolume extends IBGetOrderStatusAttr<Double> {
	
	public IBGetOrderExecVolume() {
		super();
	}

	@Override
	protected Double getEventAttr(IBEventOrderStatus event) {
		return event.getAvgFillPrice() * event.getFilled();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetOrderExecVolume.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121225, 102041).toHashCode();
	}

}
