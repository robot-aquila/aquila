package ru.prolib.aquila.core.data;

public class CandleHighSeries extends CandlePartSeries<Double> {

	public CandleHighSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".HIGH";
	}


	@Override
	protected Double getPart(Candle candle) {
		return candle.getHigh();
	}

}
