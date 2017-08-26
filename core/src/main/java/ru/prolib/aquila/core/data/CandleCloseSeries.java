package ru.prolib.aquila.core.data;

public class CandleCloseSeries extends CandlePartSeries<Double> {

	public CandleCloseSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".CLOSE";
	}

	@Override
	protected Double getPart(Candle candle) {
		return candle.getClose();
	}
}
