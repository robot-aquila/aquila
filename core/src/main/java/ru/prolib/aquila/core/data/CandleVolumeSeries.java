package ru.prolib.aquila.core.data;

public class CandleVolumeSeries implements Series<Double> {
	private final Series<Candle> candles;
	
	public CandleVolumeSeries(Series<Candle> candles) {
		super();
		this.candles = candles;
	}

	@Override
	public String getId() {
		return candles.getId() + ".VOLUME";
	}

	@Override
	public Double get() throws ValueException {
		return (double)candles.get().getVolume();
	}

	@Override
	public Double get(int index) throws ValueException {
		return (double)candles.get(index).getVolume();
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

}
