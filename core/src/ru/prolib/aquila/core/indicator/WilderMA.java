package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.DataSeries;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Wilder Moving Average (WilderMA).
 * <p>
 * Расчет выполняется по формуле:
 * <pre>
 *	MAi = (MAp * (n - 1) + Pi) / n
 * </pre>
 * Где: MAi - значение скользящей средней в текущем периоде, MAp - значение
 * скользящей средней предыдущего периода, n - период скользящей средней,
 * Pi - цена в текущем периоде. Для первого периода берется исходное значение
 * как есть.
 * <p>
 * 2013-03-12<br>
 * $Id: WilderMA.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class WilderMA extends _MA {

	public WilderMA(String id, DataSeries source, int period, int limit) {
		super(id, source, period, limit);
	}
	
	public WilderMA(String id, DataSeries source, int period) {
		this(id, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	public WilderMA(DataSeries source, int period) {
		this(null, source, period, SeriesImpl.STORAGE_NOT_LIMITED);
	}

	@Override
	protected String makeId(String id) {
		return id == null ? "WilderMA(" + period + ")" : id;
	}

	@Override
	protected Double calculate(int index) throws ValueException {
		Double value = source.get(index);
		if ( index == 0 || value == null ) {
			return value;
		}
		Double MAp = series.get(index - 1);
		if ( MAp == null ) {
			return value;
		}
		return (MAp * (period - 1) + value) / period;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != WilderMA.class ) {
			return false;
		}
		return fieldsEquals((_MA) other);
	}

}
