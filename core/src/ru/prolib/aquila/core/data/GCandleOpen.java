package ru.prolib.aquila.core.data;

/**
 * Геттер цены открытия свечи.
 * <p>
 * 2012-04-25<br>
 * $Id: GCandleOpen.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleOpen extends GCandlePart<Double> {

	@Override
	protected Double getPart(Candle candle) {
		return candle.getOpen();
	}

}
