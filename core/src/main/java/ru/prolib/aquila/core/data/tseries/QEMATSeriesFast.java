package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;
import ru.prolib.aquila.core.data.ZTFrame;

/**
 * Реализация быстрого QEMA.
 * <p>
 * Работает по принципу кеша, сохраняя рассчитанные значения QEMA для повторного
 * доступа и ускорения расчета последующих значений индикатора. Для корректной работы
 * требует дополнительный контроллер, который будет своевременно помечать диапазоны
 * значений как недействительные при добавлении, обновлении или удалении значений
 * исходной серии.
 * <p>
 * Алгоритм несколько отличается от работы алгоритма
 * {@link ru.prolib.aquila.core.data.TAMath#qema(ru.prolib.aquila.core.data.Series, int, int)}.
 * <p>
 * Прежде всего, это касается обработки null-значений исходной последовательности.
 * Появление null-значение в исходной позиции дают точно такой же результат,
 * как и для начала последовательности. Результатом расчета будет null,
 * на протяжении period-1 элементов после позиции null-значения. Например,
 * для QEMA с периодом 3 и null-значением в позиции 10 исходной последовательности,
 * позициям 10,11,12 будет null значение QEMA. Начиная с 13 позиции значение QEMA
 * будет определенным, при условии, что после 10 позиции в исходной позиции не было
 * null-значений.
 * <p>
 * В реализации {@link TAMath#qema} осуществляется пропуск period, а не period-1
 * элементов. Другим отличием является то, что {@link TAMath#qema} переносит
 * последнее полученное значение QEMA через дырку null-ей исходной позиции. Это
 * происходит по крайней мере если дырка длинной меньше period. Это необходимо
 * проверить и поправить.
 */
public class QEMATSeriesFast implements TSeries<CDecimal>, TSeriesCache {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QEMATSeriesFast.class);
	}
	
	private final String id;
	private final TSeries<CDecimal> source;
	private final int period;
	private final ArrayList<CDecimal> cache;
	private final CDecimal periodMinus1, periodPlus1;
	private int lastValidIndex = -1;
	
	QEMATSeriesFast(String id, TSeries<CDecimal> source, int period, int resultScale, ArrayList<CDecimal> cache) {
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period must be greater than 1");
		}
		this.id = id;
		this.source = source;
		this.period = period;
		this.cache = cache;
		this.periodMinus1 = CDecimalBD.of((long) period - 1).withScale(resultScale);
		this.periodPlus1 = CDecimalBD.of((long) period + 1).withScale(resultScale);
	}
	
	public QEMATSeriesFast(String id, TSeries<CDecimal> source, int period, int resultScale) {
		this(id, source, period, resultScale, new ArrayList<>());
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
	 * @return QEMA value of the last value in refreshed sequence (at "to" index)
	 * @throws ValueException an error occurred
	 */
	private CDecimal refreshCache(int from, int to) throws ValueException {
		int lengthSource = source.getLength();
		int lengthCache = cache.size();
		cache.ensureCapacity((lengthSource / 128 + 1) * 128);
		int indexSkipFirst = period - 2;
		CDecimal qemaCurr = null;
		for ( int index = from; index <= to; index ++ ) {
			if ( index <= indexSkipFirst ) {
				lengthCache = updateCache(index, null, lengthCache);
				continue;
			}
			CDecimal valueCurr = source.get(index);
			if ( valueCurr == null ) {
				lengthCache = updateCache(index, null, lengthCache);
				continue;
			}

			CDecimal qemaPrev = cache.get(index - 1);
			if ( qemaPrev != null ) {
				qemaCurr = qemaPrev.multiply(periodMinus1)
						.add(valueCurr.multiply(2L))
						.divide(periodPlus1);
			} else {
				qemaCurr = qema(index);
			}
			lengthCache = updateCache(index, qemaCurr, lengthCache);
		}
		lastValidIndex = to;
		return qemaCurr;
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
	
	/**
	 * Calculate QEMA based on source data.
	 * <p>
	 * @param index - index of element to calculate
	 * @return QEMA value at specified index
	 * @throws ValueException an error occurred
	 */
	private CDecimal qema(int index) throws ValueException {
		int indexStart = index - period + 1;
		if ( indexStart < 0 ) {
			return null;
		}
		CDecimal prev = source.get(indexStart), curr = null;
		if ( prev == null ) {
			return null;
		}
		for ( int i = indexStart + 1; i <= index; i ++ ) {
			curr = source.get(i);
			if ( curr == null ) {
				return null;
			}
			prev = prev.multiply(periodMinus1)
					.add(curr.multiply(2L))
					.divide(periodPlus1);
		}
		return prev;
	}
	
	@Override
	public Instant toKey(int index) throws ValueException {
		return source.toKey(index);
	}

}
