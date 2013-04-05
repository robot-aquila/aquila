package ru.prolib.aquila.core.data;

import java.util.Date;

/**
 * Геттер времени свечи.
 * <p>
 * 2012-05-25<br>
 * $Id: GCandleTime.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleTime extends GCandlePart<Date> {

	@Override
	protected Date getPart(Candle candle) {
		return candle.getTime();
	}

}
