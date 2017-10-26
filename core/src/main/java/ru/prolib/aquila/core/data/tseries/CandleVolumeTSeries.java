package ru.prolib.aquila.core.data.tseries;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.ValueException;

import java.time.Instant;

public class CandleVolumeTSeries implements TSeries<Long> {
	private final String id;
	private final TSeries<Candle> candles;

	public CandleVolumeTSeries(String id, TSeries<Candle> candles) {
		this.id = id;
		this.candles = candles;
	}

	public CandleVolumeTSeries(TSeries<Candle> candles) {
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
	public Long get() throws ValueException {
		Candle candle = candles.get();
		return candle == null ? null : candle.getVolume();
	}

	@Override
	public Long get(int index) throws ValueException {
		Candle candle = candles.get(index);
		return candle == null ? null : candle.getVolume();
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
	public Long get(Instant time) {
		Candle candle = candles.get(time);
		return candle == null ? null : candle.getVolume();
	}

	@Override
	public ZTFrame getTimeFrame() {
		return candles.getTimeFrame();
	}

	@Override
	public int toIndex(Instant time) {
		return candles.toIndex(time);
	}

}
