package ru.prolib.aquila.core.data;

import org.joda.time.Interval;

/**
 * Геттер интервала свечи.
 * <p>
 * 2012-05-25<br>
 * $Id: GCandleTime.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleInterval extends GCandlePart<Interval> {

	@Override
	protected Interval getPart(Candle candle) {
		return candle.getInterval();
	}

}
