package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeriesImpl;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;

public class SuperTSeries implements ObservableTSeries<Instant> {
	
	static class Entry {
		private final TSeries<?> readable;
		private final ObservableTSeries<?> observable;
		private final EditableTSeries<?> editable;
		private final boolean closeable;
		
		Entry(ObservableTSeriesImpl<?> series, boolean closeable) {
			this.readable = series;
			this.observable = series;
			this.editable = series;
			this.closeable = closeable;
		}
		
		Entry(ObservableTSeriesImpl<?> series) {
			this(series, true);
		}
		
		Entry(EditableTSeries<?> series, boolean closeable) {
			this.readable = series;
			this.observable = null;
			this.editable = series;
			this.closeable = closeable;
		}
		
		Entry(EditableTSeries<?> series) {
			this(series, true);
		}
		
		Entry(TSeries<?> series) {
			this.readable = series;
			this.observable = null;
			this.editable = null;
			this.closeable = false;
		}
		
		@SuppressWarnings("unchecked")
		public <T> TSeries<T> getReadableSeries() {
			return (TSeries<T>) readable;
		}
		
		@SuppressWarnings("unchecked")
		public <T> ObservableTSeries<T> getObservableSeries() {
			return (ObservableTSeries<T>) observable;
		}
		
		@SuppressWarnings("unchecked")
		public <T> EditableTSeries<T> getEditableSeries() {
			return (EditableTSeries<T>) editable;
		}
		
		public boolean isObservable() {
			return observable != null;
		}
		
		public boolean isEditable() {
			return editable != null;
		}
		
		public boolean isCloseable() {
			return closeable;
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
			return new EqualsBuilder()
				.append(o.readable, readable)
				.append(o.observable, observable)
				.append(o.editable, editable)
				.append(o.closeable, closeable)
				.isEquals();
		}
		
	}
	
	private final EventQueue queue;
	private final Map<String, Entry> entries;
	private final TSeriesNodeStorageKeys storage;
	
	SuperTSeries(EventQueue queue,
			TSeriesNodeStorageKeys storage,
			Map<String, Entry> entries)
	{
		this.queue = queue;
		this.storage = storage;
		this.entries = entries;
	}
	
	public SuperTSeries(String seriesID, ZTFrame tframe, EventQueue queue) {
		this(queue,
			new TSeriesNodeStorageKeys(seriesID, queue, new TSeriesNodeStorageImpl(tframe)),
			new HashMap<>());
	}
	
	public SuperTSeries(ZTFrame tframe, EventQueue queue) {
		this(queue,
			new TSeriesNodeStorageKeys(queue, new TSeriesNodeStorageImpl(tframe)),
			new HashMap<>());
	}

	@Override
	public ZTFrame getTimeFrame() {
		return storage.getTimeFrame();
	}

	@Override
	public Instant get(Instant key) {
		return storage.get(key);
	}

	@Override
	public int toIndex(Instant key) {
		return storage.toIndex(key);
	}

	@Override
	public String getId() {
		return storage.getId();
	}

	@Override
	public Instant get() throws ValueException {
		return storage.get();
	}

	@Override
	public Instant get(int index) throws ValueException {
		return storage.get(index);
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
	public EventType onUpdate() {
		return storage.onUpdate();
	}
	
	/**
	 * Get observable series by ID.
	 * <p>
	 * @param seriesID - series ID
	 * @return series instance
	 * @throws IllegalStateException if series not exists or not observable
	 */
	public synchronized <T> ObservableTSeries<T>
		getObservableSeries(String seriesID)
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
	
	/**
	 * Get series by ID.
	 * <p>
	 * @param seriesID - series ID
	 * @return series instance
	 * @throws IllegalStateException if series not exists
	 */
	public synchronized <T> TSeries<T> getSeries(String seriesID) {
		Entry entry = entries.get(seriesID);
		if ( entry == null ) {
			throw new IllegalStateException("Series not exists: " + seriesID);
		}
		return entry.getReadableSeries();
	}
	
	/**
	 * Check that series is exists.
	 * <p>
	 * @param seriesID - series ID
	 * @return true if series exists, false - otherwise
	 */
	public synchronized boolean isSeriesExists(String seriesID) {
		return entries.containsKey(seriesID);
	}
	
	/**
	 * Check that series is observable.
	 * <p>
	 * @param seriesID - series ID
	 * @return true if seires observable, false - otherwise
	 * @throws IllegalStateException if series not exists
	 */
	public synchronized boolean isSeriesObservable(String seriesID) {
		Entry entry = entries.get(seriesID);
		if ( entry == null ) {
			throw new IllegalStateException("Series not exists: " + seriesID);
		}
		return entry.isObservable();
	}
	
	/**
	 * Create new series.
	 * <p>
	 * @param seriesID - series ID.
	 * @param observable - create series observable if true. 
	 * @return new created series
	 * @throws IllegalStateException if series already exists
	 */
	public synchronized <T> EditableTSeries<T>
		createSeries(String seriesID, boolean observable)
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
	
	/**
	 * Register series.
	 * <p>
	 * Series registered with this method will not be closed on slice closing.
	 * <p>
	 * @param series - the series instance. This series may be not linked to shared storage.
	 * @throws IllegalStateException - if series already exists
	 * @throws IllegalArgumentException - the series is of different time frame
	 */
	public synchronized <T> void registerRawSeries(TSeries<T> series)
		throws IllegalStateException, IllegalArgumentException
	{
		registerSeries(series);
	}
	
	/**
	 * Register series.
	 * <p>
	 * Works same as {@link #registerRawSeries(TSeries)} but allows to override series ID.
	 * <p>
	 * @param series - series instance
	 * @param seriesID - series ID to use in scope of the data slice
	 * @throws IllegalStateException - if series already exists
	 * @throws IllegalArgumentException - the series is of different time frame
	 */
	public synchronized <T> void registerRawSeries(TSeries<T> series, String seriesID) {
		registerSeries(series, seriesID);
	}
	
	/**
	 * Register series.
	 * <p>
	 * Series registered with this method will be marked as closeable and will be closed on slice closing.
	 * <p>
	 * @param series - the series instance. This series may be not linked to shared storage.
	 * @throws IllegalStateException - if series already exists
	 * @throws IllegalArgumentException - the series is of different time frame
	 */
	public synchronized <T> void registerRawSeries(EditableTSeries<T> series) {
		registerSeries(series);
	}
	
	/**
	 * Register series.
	 * <p>
	 * Works same as {@link #registerRawSeries(EditableTSeries)} but allows to override series ID.
	 * <p>
	 * @param series - series instance
	 * @param seriesID - series ID to use in scope of the data slice
	 * @throws IllegalStateException - if series already exists
	 * @throws IllegalArgumentException - the series is of different time frame
	 */
	public synchronized <T> void registerRawSeries(EditableTSeries<T> series, String seriesID) {
		registerSeries(series, seriesID);
	}
	
	private void registerSeries(TSeries<?> series) {
		registerSeries(series, series.getId());
	}
	
	private void registerSeries(TSeries<?> series, String seriesID) {
		checkItCanBeAdded(series, seriesID);
		Entry entry = new Entry((TSeries<?>) series);
		entries.put(seriesID, entry);
	}
	
	private void registerSeries(EditableTSeries<?> series) {
		registerSeries(series, series.getId());
	}
	
	private void registerSeries(EditableTSeries<?> series, String seriesID) {
		checkItCanBeAdded(series, seriesID);
		Entry entry = new Entry((EditableTSeries<?>) series);
		entries.put(seriesID, entry);
	}
	
	private void checkItCanBeAdded(TSeries<?> series, String seriesID) {
		ZTFrame seriesTF = series.getTimeFrame(), thisTF = getTimeFrame();
		if ( ! thisTF.equals(seriesTF) ) {
			throw new IllegalArgumentException("Timeframe mismatch: SeriesID: " + seriesID
					+ ", Expected: " + thisTF + ", Actual: " + seriesTF);
		}
		Entry entry = entries.get(seriesID);
		if ( entry != null ) {
			throw new IllegalStateException("Series already exists: " + seriesID);
		}
	}

}
