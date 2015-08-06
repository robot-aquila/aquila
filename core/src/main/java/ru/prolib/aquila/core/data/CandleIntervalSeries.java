package ru.prolib.aquila.core.data;

import org.joda.time.Interval;

/**
 * Ряд временных интервалов.
 * <p>
 * Используется для конвертации данных свечей ряда в обособленный ряд.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleTimeSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleIntervalSeries implements Series<Interval> {
	private final Series<Candle> candles;

	public CandleIntervalSeries(Series<Candle> candles) {
		super();
		this.candles = candles;
	}

	@Override
	public String getId() {
		return candles.getId() + ".INTERVAL";
	}

	@Override
	public Interval get() throws ValueException {
		return candles.get().getInterval();
	}

	@Override
	public Interval get(int index) throws ValueException {
		return candles.get(index).getInterval();
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

}
