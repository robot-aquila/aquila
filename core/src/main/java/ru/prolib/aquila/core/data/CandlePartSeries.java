package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.concurrency.LID;

/**
 * Используется для конвертации данных ряда свечей в обособленный ряд.
 */
public abstract class CandlePartSeries<T> implements Series<T> {
	protected final Series<Candle> candles;

	public CandlePartSeries(Series<Candle> candles) {
		super();
		this.candles = candles;
	}

	@Override
	public T get() throws ValueException {
		Candle candle = candles.get();
		if(candle == null) {
			return null;
		}
		return getPart(candle);
	}

	@Override
	public T get(int index) throws ValueException {
		Candle candle = candles.get(index);
		if(candle == null) {
			return null;
		}
		return getPart(candle);
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

	protected abstract T getPart(Candle candle);
}
