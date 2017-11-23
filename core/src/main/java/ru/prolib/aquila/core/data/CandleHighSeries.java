package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class CandleHighSeries extends CandlePartSeries<CDecimal> {

	public CandleHighSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".HIGH";
	}


	@Override
	protected CDecimal getPart(Candle candle) {
		return candle.getHigh();
	}

}
