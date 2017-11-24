package ru.prolib.aquila.core.utils;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Simple implementation of the price scale DB.
 * It used as simple data cache: set a scale before its actual usage.
 */
public class PriceScaleDBImpl implements PriceScaleDB {
	private final Map<Symbol, Integer> scaleMap;
	
	public PriceScaleDBImpl() {
		scaleMap = new HashMap<>();
	}
	
	public synchronized void setScale(Symbol symbol, int scale) {
		scaleMap.put(symbol, scale);
	}

	@Override
	public synchronized int getScale(Symbol symbol) {
		Integer scale = scaleMap.get(symbol);
		if ( scale == null ) {
			throw new IllegalArgumentException("Price scale not found for symbol: " + symbol);
		}
		return scale;
	}

}
