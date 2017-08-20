package ru.prolib.aquila.core.data;

import java.time.Instant;

import ru.prolib.aquila.core.concurrency.LID;

public class CandleStartTimeSeries implements Series<Instant> {
	private final Series<Candle> candles;

	public CandleStartTimeSeries(Series<Candle> candles) {
		super();
		this.candles = candles;
	}

	@Override
	public String getId() {
		return candles.getId() + ".START_TIME";
	}

	@Override
	public Instant get() throws ValueException {
		return candles.get().getStartTime();
	}

	@Override
	public Instant get(int index) throws ValueException {
		return candles.get(index).getStartTime();
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
