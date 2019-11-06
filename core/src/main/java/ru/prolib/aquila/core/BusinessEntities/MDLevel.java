package ru.prolib.aquila.core.BusinessEntities;

/**
 * Market data level.
 */
public enum MDLevel {
	
	/**
	 * Minimal available data. Only primary attributes.
	 */
	L0,
	
	/**
	 * Level-1 data including all primary data like main attributes and best bid and
	 * best offer market data only.
	 */
	L1_BBO,
	
	/**
	 * Level-1 data as usual. All primary data like main attributes, best bid, best
	 * offer and all trades.
	 */
	L1,
	
	/**
	 * Maximum available data. All primary data like main attributes + level 1 data
	 * like best bid, best offer and all trades + depth of market level 2 data.
	 */
	L2
}
