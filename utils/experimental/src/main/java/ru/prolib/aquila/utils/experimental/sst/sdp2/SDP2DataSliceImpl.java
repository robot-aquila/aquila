package ru.prolib.aquila.utils.experimental.sst.sdp2;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeriesImpl;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorageImpl;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorageKeys;

public class SDP2DataSliceImpl<K extends SDP2Key> implements SDP2DataSlice<K> {
	
	static class Entry {
		private final TSeries<?> readable;
		private final ObservableTSeries<?> observable;
		
		Entry(ObservableTSeriesImpl<?> series) {
			this.readable = series;
			this.observable = series;
		}
		
		Entry(TSeries<?> series) {
			this.readable = series;
			this.observable = null;
		}
		
		@SuppressWarnings("unchecked")
		public <T> TSeries<T> getReadableSeries() {
			return (TSeries<T>) readable;
		}
		
		@SuppressWarnings("unchecked")
		public <T> ObservableTSeries<T> getObservableSeries() {
			return (ObservableTSeries<T>) observable;
		}
		
		public boolean isObservable() {
			return observable != null;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != Entry.class ) {
				return false;
			}
			Entry o = (Entry) other;
			return o.readable == readable && o.observable == observable;
		}
		
	}
	
	private final K key;
	private final EventQueue queue;
	private final Map<String, Entry> entries;
	private final TSeriesNodeStorageKeys storage;
	
	/**
	 * Service constructor.
	 * For testing purposes only.
	 * <p>
	 * @param key - key of the slice
	 * @param queue - event queue to build observable series
	 * @param entries - map of series
	 * @param storage - node storage
	 */
	SDP2DataSliceImpl(K key, EventQueue queue, TSeriesNodeStorageKeys storage,
			Map<String, Entry> entries)
	{
		this.key = key;
		this.queue = queue;
		this.entries = entries;
		this.storage = storage;
	}
	
	public SDP2DataSliceImpl(K key, EventQueue queue) {
		this(key, queue, new TSeriesNodeStorageKeys(queue, new TSeriesNodeStorageImpl(key.getTimeFrame())), new HashMap<>());
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public Symbol getSymbol() {
		return key.getSymbol();
	}

	@Override
	public TimeFrame getTimeFrame() {
		return key.getTimeFrame();
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}
	
	public TSeriesNodeStorage getStorage() {
		return storage;
	}
	
	@Override
	public synchronized
		ObservableTSeries<Instant> getIntervalStartSeries()
	{
		return storage;
	}
	
	@Override
	public synchronized <T>
		EditableTSeries<T> createSeries(String seriesID, boolean observable)
			throws IllegalStateException
	{
		Entry entry = entries.get(seriesID);
		if ( entry != null ) {
			throw new IllegalStateException("Series already exists: " + seriesID);
		}
		EditableTSeries<T> series = null;
		if ( observable ) {
			series = new ObservableTSeriesImpl<T>(queue, new TSeriesImpl<T>(seriesID, storage));
			entry = new Entry((ObservableTSeriesImpl<T>) series); // force ctor
		} else {
			entry = new Entry(series = new TSeriesImpl<T>(seriesID, storage));
		}
		entries.put(seriesID, entry);
		return series;
	}
	
	@Override
	public synchronized <T> void registerRawSeries(TSeries<T> series)
			throws IllegalStateException
	{
		String seriesID = series.getId();
		Entry entry = entries.get(seriesID);
		if ( entry != null ) {
			throw new IllegalStateException("Series already exists: " + seriesID);
		}
		entry = new Entry(series);
		entries.put(seriesID, entry);
	}
	
	@Override
	public synchronized <T>
		ObservableTSeries<T> getObservableSeries(String seriesID)
			throws IllegalStateException
	{
		Entry entry = entries.get(seriesID);
		if ( entry == null ) {
			throw new IllegalStateException("Series not exists: " + seriesID);
		}
		if ( ! entry.isObservable() ) {
			throw new IllegalStateException("Series not observable: " + seriesID);
		}
		return entry.getObservableSeries();
	}

	@Override
	public synchronized <T> TSeries<T> getSeries(String seriesID)
		throws IllegalStateException
	{
		Entry entry = entries.get(seriesID);
		if ( entry == null ) {
			throw new IllegalStateException("Series not exists: " + seriesID);
		}
		return entry.getReadableSeries();
	}

	@Override
	public synchronized boolean isExists(String seriesID) {
		return entries.containsKey(seriesID);
	}

	@Override
	public synchronized boolean isObservable(String seriesID) {
		Entry entry = entries.get(seriesID);
		if ( entry == null ) {
			throw new IllegalStateException("Series not exists: " + seriesID);
		}
		return entry.isObservable();
	}

}
