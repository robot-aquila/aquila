package ru.prolib.aquila.core.data;

public class CandleOpenSeries implements Series<Double> {
	private final Series<Candle> candles;
	
	public CandleOpenSeries(Series<Candle> candles) {
		super();
		this.candles = candles;
	}

	@Override
	public String getId() {
		return candles.getId() + ".OPEN";
	}

	@Override
	public Double get() throws ValueException {
		return candles.get().getOpen();
	}

	@Override
	public Double get(int index) throws ValueException {
		return candles.get(index).getOpen();
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

}
