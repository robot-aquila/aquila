package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class CandleVolumeSeries extends CandlePartSeries<CDecimal> {

	public CandleVolumeSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".VOLUME";
	}

	@Override
	protected CDecimal getPart(Candle candle) {
		return candle.getVolume();
	}
}
