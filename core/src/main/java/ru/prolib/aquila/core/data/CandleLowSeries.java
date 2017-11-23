package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class CandleLowSeries extends CandlePartSeries<CDecimal> {

	public CandleLowSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".LOW";
	}

	@Override
	protected CDecimal getPart(Candle candle) {
		return candle.getLow();
	}
}
