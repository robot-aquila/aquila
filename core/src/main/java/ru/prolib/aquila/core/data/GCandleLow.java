package ru.prolib.aquila.core.data;

/**
 * Геттер минимума свечи.
 * <p>
 * 2012-04-25<br>
 * $Id: GCandleLow.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleLow extends GCandlePart<Double> {

	@Override
	protected Double getPart(Candle candle) {
		return candle.getLow();
	}

}
