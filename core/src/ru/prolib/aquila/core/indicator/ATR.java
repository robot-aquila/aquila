package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.*;

/**
 * ATR - Average True Range.
 */
public class ATR extends WilderMA {

	public ATR(String id, CandleSeries candles, int period, int limit) {
		super(id, new TR(candles), period, limit);
	}
	
	public ATR(String id, CandleSeries candles, int period) {
		this(id, candles, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	public ATR(CandleSeries candles, int period) {
		this(null, candles, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	@Override
	protected String makeId(String id) {
		return id == null ? "ATR(" + period + ")" : id;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ATR.class ) {
			return false;
		}
		return fieldsEquals((_MA) other);
	}

}
