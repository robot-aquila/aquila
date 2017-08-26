package ru.prolib.aquila.core.data;

public class CandleVolumeSeries extends CandlePartSeries<Long> {

	public CandleVolumeSeries(Series<Candle> candles) {
		super(candles);
	}

	@Override
	public String getId() {
		return candles.getId() + ".VOLUME";
	}

	@Override
	protected Long getPart(Candle candle) {
		return candle.getVolume();
	}
}
