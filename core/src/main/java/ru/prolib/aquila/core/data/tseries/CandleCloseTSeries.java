package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;

public class CandleCloseTSeries implements TSeries<Double> {
	private final String id;
	private final TSeries<Candle> candles;
	
	public CandleCloseTSeries(String id, TSeries<Candle> candles) {
		this.id = id;
		this.candles = candles;
	}
	
	public CandleCloseTSeries(TSeries<Candle> candles) {
		this(TSeries.DEFAULT_ID, candles);
	}
	
	public TSeries<Candle> getCandleSeries() {
		return candles;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Double get() throws ValueException {
		Candle candle = candles.get();
		return candle == null ? null : candle.getClose();
	}

	@Override
	public Double get(int index) throws ValueException {
		Candle candle = candles.get(index);
		return candle == null ? null : candle.getClose();
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

	@Override
	public Double get(Instant time) {
		Candle candle = candles.get(time);
		return candle == null ? null : candle.getClose();
	}

	@Override
	public TimeFrame getTimeFrame() {
		return candles.getTimeFrame();
	}

	@Override
	public int toIndex(Instant time) {
		return candles.toIndex(time);
	}

}