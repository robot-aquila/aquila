package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class CandleCloseSeries extends CandlePartSeries<CDecimal> {

	public CandleCloseSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".CLOSE";
	}

	@Override
	protected CDecimal getPart(Candle candle) {
		return candle.getClose();
	}
}
