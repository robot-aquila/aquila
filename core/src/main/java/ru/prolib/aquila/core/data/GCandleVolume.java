package ru.prolib.aquila.core.data;

/**
 * Геттер объема свечи.
 * <p>
 * 2012-05-25<br>
 * $Id: GCandleVolume.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class GCandleVolume extends GCandlePart<Double> {

	@Override
	protected Double getPart(Candle candle) {
		return (double) candle.getVolume();
	}

}
