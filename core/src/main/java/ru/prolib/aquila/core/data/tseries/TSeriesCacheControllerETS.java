package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;

/**
 * Cache controller based on decorating editable series. This class wraps mutators
 * of editable series and delegates cache invalidation calls to controlled cache.
 * To make it work instance of this class should be used as entry point to take all
 * updates instead of editable series which is basis for this class. This class may
 * control more than one instances of cache.
 */
public class TSeriesCacheControllerETS<T> implements EditableTSeries<T>, TSeriesCacheController<T> {
	private final EditableTSeries<T> series;
	private final List<TSeriesCache> caches;
	
	TSeriesCacheControllerETS(EditableTSeries<T> series, List<TSeriesCache> caches) {
		this.series = series;
		this.caches = caches;
	}
	
	public TSeriesCacheControllerETS(EditableTSeries<T> series) {
		this(series, new ArrayList<>());
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesCacheController#addCache(ru.prolib.aquila.core.data.tseries.TSeriesCache)
	 */
	@Override
	public synchronized TSeriesCacheController<T> addCache(TSeriesCache cache) {
		caches.add(cache);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.data.tseries.TSeriesCacheController#removeCache(ru.prolib.aquila.core.data.tseries.TSeriesCache)
	 */
	@Override
	public synchronized TSeriesCacheController<T> removeCache(TSeriesCache cache) {
		caches.remove(cache);
		return this;
	}

	@Override
	public ZTFrame getTimeFrame() {
		return series.getTimeFrame();
	}

	@Override
	public T get(Instant key) {
		return series.get(key);
	}

	@Override
	public int toIndex(Instant key) {
		return series.toIndex(key);
	}
	
	@Override
	public Instant toKey(int index) throws ValueException {
		return series.toKey(index);
	}

	@Override
	public String getId() {
		return series.getId();
	}

	@Override
	public T get() throws ValueException {
		return series.get();
	}

	@Override
	public T get(int index) throws ValueException {
		return series.get(index);
	}

	@Override
	public int getLength() {
		return series.getLength();
	}

	@Override
	public LID getLID() {
		return series.getLID();
	}

	@Override
	public void lock() {
		series.lock();
	}

	@Override
	public void unlock() {
		series.unlock();
	}

	@Override
	public TSeriesUpdate set(Instant time, T value) {
		TSeriesUpdate update = series.set(time, value);
		int index = update.getNodeIndex();
		synchronized ( this ) {
			for ( TSeriesCache cache : caches ) {
				cache.invalidate(index);				
			}
		}
		return update;
	}

	@Override
	public void clear() {
		series.clear();
		synchronized ( this ) {
			for ( TSeriesCache cache : caches ) {
				cache.invalidate(0);
				cache.shrink();
			}
		}
	}

}
