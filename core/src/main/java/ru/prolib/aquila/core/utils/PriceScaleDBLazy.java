package ru.prolib.aquila.core.utils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Lazy initialized price scale database. This wrapper is used to provide database instance before it actual initialization.
 * For example it can be used in combination with terminal-based implementation to build a data provider which required to
 * build terminal itself. 
 */
public class PriceScaleDBLazy implements PriceScaleDB {
	private PriceScaleDB parent;
	
	public synchronized void setParentDB(PriceScaleDB parent) {
		this.parent = parent;
	}

	@Override
	public synchronized int getScale(Symbol symbol) {
		if ( parent == null ) {
			throw new IllegalStateException("Parent database is not specified");
		}
		return parent.getScale(symbol);
	}

}
