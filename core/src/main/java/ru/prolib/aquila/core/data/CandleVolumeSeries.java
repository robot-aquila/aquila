package ru.prolib.aquila.core.data;

public class CandleVolumeSeries implements Series<Long> {
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
	public Long get() throws ValueException {
		return candles.get().getVolume();
	}

	@Override
	public Long get(int index) throws ValueException {
		return candles.get(index).getVolume();
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

}
