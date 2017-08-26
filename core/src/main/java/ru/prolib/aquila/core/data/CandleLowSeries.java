package ru.prolib.aquila.core.data;

public class CandleLowSeries extends CandlePartSeries<Double> {

	public CandleLowSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".LOW";
	}

	@Override
	protected Double getPart(Candle candle) {
		return candle.getLow();
	}
}
