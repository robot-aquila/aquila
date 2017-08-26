package ru.prolib.aquila.core.data;

import java.time.Instant;

public class CandleStartTimeSeries extends CandlePartSeries<Instant> {

	public CandleStartTimeSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".START_TIME";
	}

	@Override
	protected Instant getPart(Candle candle) {
		return candle.getStartTime();
	}
}
