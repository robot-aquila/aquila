package ru.prolib.aquila.core.data;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage;

public class TSeriesImpl<T> implements EditableTSeries<T> {
	private final String id;
	private final int seriesID;
	private final TSeriesNodeStorage storage;
	
	public TSeriesImpl(String id, TSeriesNodeStorage storage) {
		this.id = id;
		this.storage = storage;
		this.seriesID = storage.registerSeries();
	}
	
	public TSeriesImpl(String id, TimeFrame timeFrame) {
		this(id, new TSeriesNodeStorage(timeFrame));
	}
	
	public TSeriesImpl(TimeFrame timeFrame) {
		this(Series.DEFAULT_ID, timeFrame);
	}
	
	public TSeriesNodeStorage getStorage() {
		return storage;
	}
	
	public int getSeriesID() {
		return seriesID;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Instant time) {
		return (T) storage.get(time, seriesID);
	}

	@Override
	public TimeFrame getTimeFrame() {
		return storage.getTimeFrame();
	}

	@Override
	public String getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		return (T) storage.get(seriesID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) throws ValueException {
		return (T) storage.get(index, seriesID);
	}

	@Override
	public int getLength() {
		return storage.getLength();
	}

	@Override
	public LID getLID() {
		return storage.getLID();
	}

	@Override
	public void lock() {
		storage.lock();
	}

	@Override
	public void unlock() {
		storage.unlock();
	}

	@Override
	public TSeriesUpdate set(Instant time, T value) {
		return storage.set(time, seriesID, value);
	}

	@Override
	public void clear() {
		storage.clear();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSeriesImpl.class ) {
			return false;
		}
		TSeriesImpl<?> o = (TSeriesImpl<?>) other;
		return new EqualsBuilder()
				.append(id, o.id)
				.append(seriesID, o.seriesID)
				.append(storage, o.storage)
				.isEquals();
	}

}
