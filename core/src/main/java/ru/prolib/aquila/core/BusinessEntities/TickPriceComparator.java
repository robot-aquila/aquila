package ru.prolib.aquila.core.BusinessEntities;

import java.util.Comparator;

public class TickPriceComparator implements Comparator<Tick> {
	/**
	 * Compare ask prices.
	 */
	public static final TickPriceComparator ASK = new TickPriceComparator(true);
	/**
	 * Compare bid prices.
	 */
	public static final TickPriceComparator BID = new TickPriceComparator(false);
	
	private final int multiplier;
	
	public TickPriceComparator(boolean ascending) {
		super();
		this.multiplier = ascending ? 1 : -1;
	}

	@Override
	public int compare(Tick a, Tick b) {
		double ap = a.getPrice(), bp = b.getPrice();
		return (ap < bp ? -1 : ap == bp ? 0 : 1) * multiplier;
	}

}