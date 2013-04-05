package ru.prolib.aquila.stat;

import ru.prolib.aquila.ChaosTheory.Order;

/**
 * Изменение позиции.
 */
@Deprecated
public class TrackingPositionChange {
	private final Order order;
	private final int barIndex;
	
	public TrackingPositionChange(Order order, int barIndex)
		throws TrackingException
	{
		super();
		this.order = order;
		this.barIndex = barIndex;
	}
	
	public Order getOrder() {
		return order;
	}
	
	public int getBarIndex() {
		return barIndex;
	}

}
