package ru.prolib.aquila.core.data;

public class CandleCloseSeries implements Series<Double> {
	private final Series<Candle> candles;
	
	public CandleCloseSeries(Series<Candle> candles) {
		super();
		this.candles = candles; 
	}

	@Override
	public String getId() {
		return candles.getId() + ".CLOSE";
	}

	@Override
	public Double get() throws ValueException {
		return candles.get().getClose();
	}

	@Override
	public Double get(int index) throws ValueException {
		return candles.get(index).getClose();
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

}
