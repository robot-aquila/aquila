package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.concurrency.LID;

public class CandleHighSeries implements Series<Double> {
	private final Series<Candle> candles;
	
	public CandleHighSeries(Series<Candle> candles) {
		super();
		this.candles = candles;
	}

	@Override
	public String getId() {
		return candles.getId() + ".HIGH";
	}

	@Override
	public Double get() throws ValueException {
		return candles.get().getHigh();
	}

	@Override
	public Double get(int index) throws ValueException {
		return candles.get(index).getHigh();
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

	@Override
	public LID getLID() {
		return candles.getLID();
	}

	@Override
	public void lock() {
		candles.lock();
	}

	@Override
	public void unlock() {
		candles.unlock();
	}

}
