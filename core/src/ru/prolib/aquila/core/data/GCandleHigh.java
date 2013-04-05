package ru.prolib.aquila.core.data;

/**
 * Геттер максимума свечи.
 * <p>
 * 2012-04-25<br>
 * $Id: GCandleHigh.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleHigh extends GCandlePart<Double> {

	@Override
	protected Double getPart(Candle candle) {
		return candle.getHigh();
	}

}
