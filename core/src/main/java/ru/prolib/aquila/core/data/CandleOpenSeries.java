package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class CandleOpenSeries extends CandlePartSeries<CDecimal> {

	public CandleOpenSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".OPEN";
	}

	@Override
	protected CDecimal getPart(Candle candle) {
		return candle.getOpen();
	}
}
