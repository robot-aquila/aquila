package ru.prolib.aquila.core.data;

import org.threeten.extra.Interval;

/**
 * Ряд временных интервалов.
 * <p>
 * Используется для конвертации данных свечей ряда в обособленный ряд.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleTimeSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleIntervalSeries extends CandlePartSeries<Interval> {

	public CandleIntervalSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".INTERVAL";
	}

	@Override
	protected Interval getPart(Candle candle) {
		return candle.getInterval();
	}

}
