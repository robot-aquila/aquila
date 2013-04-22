package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * Геттер средней цены исполненного части заявки из события
 * {@link IBEventOrderStatus}.
 */
public class IBGetOrderAvgExecPrice extends IBGetOrderStatusAttr<Double> {
	
	public IBGetOrderAvgExecPrice() {
		super();
	}

	@Override
	protected Double getEventAttr(IBEventOrderStatus event) {
		return event.getAvgFillPrice();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetOrderAvgExecPrice.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130423, 170643).toHashCode();
	}

}
