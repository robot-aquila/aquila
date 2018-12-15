package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;
import ru.prolib.aquila.core.data.ZTFrame;

public class QATRTSeriesFast implements TSeries<CDecimal>, TSeriesCache {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QATRTSeriesFast.class);
	}
	
	private final String id;
	private final TSeries<Candle> source;
	private final int period, periodMinus1;
	private final ArrayList<CDecimal> cache;
	private final CDecimal d_periodMinus1;
	private final CDecimal d_period;
	private final TAMath math = TAMath.getInstance();
	private int lastValidIndex = -1;
	
	public QATRTSeriesFast(String id,
			TSeries<Candle> source,
			int period,
			int resultScale,
			ArrayList<CDecimal> cache)
	{
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period must be greater than 1");
		}
		this.id = id;
		this.source = source;
		this.period = period;
		this.periodMinus1 = period - 1;
		this.cache = cache;
		d_period = CDecimalBD.of((long)period).withScale(resultScale);
		d_periodMinus1 = CDecimalBD.of((long)periodMinus1).withScale(resultScale);
	}
	
	public QATRTSeriesFast(String id,
			TSeries<Candle> source,
			int result,
			int resultScale)
	{
		this(id, source, result, resultScale, new ArrayList<>());
	}

	@Override
	public int toIndex(Instant key) {
		return source.toIndex(key);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getLength() {
		return source.getLength();
	}

	@Override
	public LID getLID() {
		return source.getLID();
	}

	@Override
	public void lock() {
		source.lock();
	}

	@Override
	public void unlock() {
		source.unlock();
	}
	
	@Override
	public ZTFrame getTimeFrame() {
		return source.getTimeFrame();
	}

	@Override
	public CDecimal get(int index) throws ValueException {
		lock();
		try {
			if ( index < 0 ) {
				index = source.getLength() - 1 + index;
			}
			if ( index < 0 ) {
				throw new ValueOutOfRangeException("For index: " + index);
			}
			if ( index > lastValidIndex ) {
				return refreshCache(lastValidIndex + 1, index);
			} else {
				return cache.get(index);
			}
		} finally {
			unlock();
		}
	}

	@Override
	public CDecimal get() throws ValueException {
		lock();
		try {
			return get(getLength() - 1);
		} finally {
			unlock();
		}
	}

	@Override
	public CDecimal get(Instant key) {
		lock();
		try {
			int r = toIndex(key);
			return r < 0 ? null : get(r);
		} catch ( ValueException e ) {
			logger.error("Unexpected exception: ", e);
			return null;
		} finally {
			unlock();
		}
	}

	@Override
	public void invalidate(int indexFrom) {
		lock();
		try {
			if ( indexFrom <= lastValidIndex ) {
				lastValidIndex = indexFrom - 1;
			}
		} finally {
			unlock();
		}
	}

	@Override
	public void shrink() {
		lock();
		try {
			if ( lastValidIndex < 0 ) {
				cache.clear();
			} else if ( lastValidIndex < cache.size() - 1 ) {
				cache.subList(lastValidIndex + 1, cache.size()).clear();
			}
		} finally {
			unlock();
		}
	}
	
	public int getLastValidIndex() {
		lock();
		try {
			return lastValidIndex;
		} finally {
			unlock();
		}
	}
	
	/**
	 * Refresh cached data for specified range.
	 * <p>
	 * @param from - index to refresh from (inclusive)
	 * @param to - index to refresh to (inclusive)
	 * @return value of the last value in refreshed sequence (at "to" index)
	 * @throws ValueException an error occurred
	 */
	private CDecimal refreshCache(int from, int to) throws ValueException {
		int lengthSource = source.getLength();
		int lengthCache = cache.size();
		cache.ensureCapacity((lengthSource / 128 + 1) * 128);
		CDecimal curr = null;
		for ( int index = from; index <= to; index ++ ) {
			if ( index < periodMinus1 ) {
				curr = null;
			} else {
				if ( index == periodMinus1 ) {
					curr = CDecimalBD.ZERO;
					for ( int i = 0; i < period; i ++ ) {
						curr = curr.add(math.tr(source, i));
					}
					curr = curr.divide(d_period);
				} else {
					curr = cache.get(index - 1)
							.multiply(d_periodMinus1)
							.add(math.tr(source, index))
							.divide(d_period);
				}
			}
			lengthCache = updateCache(index, curr, lengthCache);
		}
		lastValidIndex = to;
		return curr;
	}
	
	/**
	 * Update the cache at specified index.
	 * <p>
	 * @param index - index to update
	 * @param value - value to set
	 * @param lengthCache - current cache length
	 * @return new length of cache sequence
	 */
	private int updateCache(int index, CDecimal value, int lengthCache) {
		if ( index < lengthCache ) {
			cache.set(index, value);
		} else {
			cache.add(value);
			lengthCache ++;
		}
		return lengthCache;
	}
	
	@Override
	public Instant toKey(int index) throws ValueException {
		return source.toKey(index);
	}

}
