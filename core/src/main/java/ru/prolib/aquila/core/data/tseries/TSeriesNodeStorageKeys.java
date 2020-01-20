package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.ValueException;

public class TSeriesNodeStorageKeys implements ObservableTSeries<Instant>, TSeriesNodeStorage {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TSeriesNodeStorageKeys.class);
	}
	
	private final String id;
	private final EventQueue queue;
	private final TSeriesNodeStorage storage;
	private final EventType onUpdate, onLengthUpdate;
	
	public TSeriesNodeStorageKeys(String id, EventQueue queue, TSeriesNodeStorage storage) {
		this.id = id;
		this.queue = queue;
		this.storage = storage;
		this.onUpdate = new EventTypeImpl(id + ".UPDATE");
		this.onLengthUpdate = new EventTypeImpl(id + ".LENGTH_UPDATE");
	}

	public TSeriesNodeStorageKeys(EventQueue queue, TSeriesNodeStorage storage) {
		this(TSeries.DEFAULT_ID, queue, storage);
	}
	
	public TSeriesNodeStorage getStorage() {
		return storage;
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}
	
	@Override
	public Instant get(Instant time) {
		lock();
		try {
			int r = storage.getIntervalIndex(time);
			return r < 0 ? null : storage.getIntervalStart(r);
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
			return null;
		} finally {
			unlock();
		}
	}

	@Override
	public ZTFrame getTimeFrame() {
		return storage.getTimeFrame();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Instant get() throws ValueException {
		lock();
		try {
			int l = storage.getLength();
			return l == 0 ? null : storage.getIntervalStart(l - 1);
		} finally {
			unlock();
		}
	}

	@Override
	public Instant get(int index) throws ValueException {
		lock();
		try {
			return storage.getIntervalStart(index);
		} finally {
			unlock();
		}
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
	public int registerSeries() {
		return storage.registerSeries();
	}
	
	@Override
	public int getLastSeriesID() {
		return storage.getLastSeriesID();
	}

	@Override
	public TSeriesUpdate setValue(Instant time, int seriesID, Object value) {
		lock();
		try {
			TSeriesUpdate update = storage.setValue(time, seriesID, value);
			boolean newNode = update.isNewNode(), changed = update.hasChanged();
			if ( newNode || changed ) {
				Instant key = update.getInterval().getStart();
				TSeriesUpdateImpl serviceUpdate = new TSeriesUpdateImpl(update.getInterval())
					.setNodeIndex(update.getNodeIndex())
					.setNewNode(newNode)
					.setOldValue(newNode ? null : key)
					.setNewValue(key);
				EventFactory factory = new TSeriesUpdateEventFactory(serviceUpdate);
				queue.enqueue(onUpdate, factory);
				if ( newNode ) {
					queue.enqueue(onLengthUpdate, factory);
				}
			}
			return update;
		} finally {
			unlock();
		}
	}

	@Override
	public Object getValue(Instant time, int seriesID) {
		return storage.getValue(time, seriesID);
	}

	@Override
	public Object getValue(int index, int seriesID) throws ValueException {
		return storage.getValue(index, seriesID);
	}

	@Override
	public Object getValue(int seriesID) {
		return storage.getValue(seriesID);
	}

	@Override
	public void clear() {
		storage.clear();
	}
	
	@Override
	public void truncate(int length) {
		storage.truncate(length);
	}

	@Override
	public EventType onUpdate() {
		return onUpdate;
	}
	
	@Override
	public EventType onLengthUpdate() {
		return onLengthUpdate;
	}

	@Override
	public Instant getIntervalStart(int index) throws ValueException {
		return storage.getIntervalStart(index);
	}

	@Override
	public int getIntervalIndex(Instant time) {
		return storage.getIntervalIndex(time);
	}

	@Override
	public int toIndex(Instant time) {
		return storage.getIntervalIndex(time);
	}
	
	@Override
	public Instant toKey(int index) throws ValueException {
		return getIntervalStart(index);
	}

	@Override
	public Instant getFirstBefore(Instant time) {
		lock();
		try {
			int index = storage.getFirstIndexBefore(time);
			return index < 0 ? null : getIntervalStart(index);
		} catch ( ValueException e ) {
			return null;
		} finally {
			unlock();
		}
	}

	@Override
	public Object getFirstValueBefore(Instant time, int seriesID) {
		return storage.getFirstValueBefore(time, seriesID);
	}

	@Override
	public int getFirstIndexBefore(Instant time) {
		return storage.getFirstIndexBefore(time);
	}

}
