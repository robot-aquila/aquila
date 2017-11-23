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
		CDecimal ap = a.getPrice(), bp = b.getPrice();
		int r = ap.compareTo(bp);
		if ( r == 0 ) {
			return 0;
		} else if ( r < 0 ) {
			return -1 * multiplier;
		} else {
			return 1 * multiplier;
		}
	}

}