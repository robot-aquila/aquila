package ru.prolib.aquila.data.storage.ohlcv;

import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.data.Candle;

/**
 * Iterator with precise time limits is intended to filter elements of underlying
 * iterator using specified period. Both start and end time of period are optional.
 * It will skip heading candles which start time is less than period start time.
 * It will skip trailing candles which start time is greater or equals to period end time.
 */
public class PreciseTimeLimitsIterator implements CloseableIterator<Candle> {
	private final CloseableIterator<Candle> underlying;
	private final Instant periodStart, periodEnd;
	private boolean started = false, finished = false, closed = false;
	
	public PreciseTimeLimitsIterator(CloseableIterator<Candle> underlying,
			Instant periodStart, Instant periodEnd)
	{
		this.underlying = underlying;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			underlying.close();
			finished = closed = true;
		}
	}

	@Override
	public boolean next() throws IOException {
		if ( finished ) {
			return false;
		}
		if ( ! started ) {
			started = true;
			if ( periodStart != null ) {
				while ( underlying.next() ) {
					Candle x = underlying.item();
					if ( x.getStartTime().compareTo(periodStart) >= 0 ) {
						return true;
					}
				}
				finished = true;
				return false;
			}
		}
		if ( ! underlying.next() ) {
			finished = true;
			return false;
		}
		if ( periodEnd == null ) {
			return true;
		}
		Candle x = underlying.item();
		if ( x.getStartTime().compareTo(periodEnd) >= 0 ) {
			finished = true;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Candle item() throws IOException, NoSuchElementException {
		if ( ! started || finished ) {
			throw new NoSuchElementException();
		}
		return underlying.item();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PreciseTimeLimitsIterator.class ) {
			return false;
		}
		PreciseTimeLimitsIterator o = (PreciseTimeLimitsIterator) other;
		return new EqualsBuilder()
				.append(o.closed, closed)
				.append(o.finished, finished)
				.append(o.started, started)
				.append(o.underlying, underlying)
				.append(o.periodStart, periodStart)
				.append(o.periodEnd, periodEnd)
				.isEquals();
	}

}
