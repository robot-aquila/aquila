package ru.prolib.aquila.core.data;

/**
 * Геттер цены закрытия свечи.
 * <p>
 * 2012-04-25<br>
 * $Id: GCandleClose.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleClose extends GCandlePart<Double> {

	@Override
	protected Double getPart(Candle candle) {
		return candle.getClose();
	}

}
