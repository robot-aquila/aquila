package ru.prolib.aquila.core.data;

public class CandleOpenSeries extends CandlePartSeries<Double> {

	public CandleOpenSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".OPEN";
	}

	@Override
	protected Double getPart(Candle candle) {
		return candle.getOpen();
	}
}
