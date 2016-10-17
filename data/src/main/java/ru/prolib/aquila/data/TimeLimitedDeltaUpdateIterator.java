package ru.prolib.aquila.data;

import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.StateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;

/**
 * Time limited delta-update iterator.
 * <p>
 * This iterator limits an update sequence to the specified period. The sequence
 * may also be limited to time from only.
 */
public class TimeLimitedDeltaUpdateIterator implements CloseableIterator<DeltaUpdate> {
	private final CloseableIterator<DeltaUpdate> source;
	private final Instant startTime, endTime;
	private boolean initialUpdate = true, closed = false;
	private DeltaUpdate lastUpdate, pendingUpdate;
	
	/**
	 * Constructor.
	 * <p>
	 * @param source - the source iterator
	 * @param startTime - period start time (inclusive)
	 * @param endTime - period end time (exclusive)
	 */
	public TimeLimitedDeltaUpdateIterator(CloseableIterator<DeltaUpdate> source,
			Instant startTime, Instant endTime)
	{
		if ( endTime != null && ! endTime.isAfter(startTime) ) {
			throw new IllegalArgumentException("End time should be greater than start time");
		}
		this.source = source;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param source - the source iterator
	 * @param startTime - period start time (inclusive)
	 */
	public TimeLimitedDeltaUpdateIterator(CloseableIterator<DeltaUpdate> source,
			Instant startTime)
	{
		this(source, startTime, null);
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			IOUtils.closeQuietly(source);
			closed = true;
		}
	}

	@Override
	public boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		lastUpdate = null;
		if ( initialUpdate ) {
			initialUpdate = false;
			UpdatableStateContainer container = new UpdatableStateContainerImpl("");
			for ( ;; ) {
				if ( ! source.next() ) {
					if ( ! container.hasData() ) {
						return false;
					}
					lastUpdate = toInitialUpdate(container);
					return true;
				}
				DeltaUpdate next = source.item();
				if ( ! next.getTime().isAfter(startTime) ) {
					container.consume(next);
				} else if ( container.hasData() ) {
					pendingUpdate = next;
					lastUpdate = toInitialUpdate(container);
					return true;
				} else {
					lastUpdate = next;
					return testLastUpdateIsInPeriod();
				}
			}
		}
		if ( pendingUpdate != null ) {
			lastUpdate = pendingUpdate;
			pendingUpdate = null;
			return testLastUpdateIsInPeriod();
		}
		if ( ! source.next() ) {
			return false;
		}
		lastUpdate = source.item();
		return testLastUpdateIsInPeriod();
	}
		
	@Override
	public DeltaUpdate item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		if ( lastUpdate == null ) {
			throw new NoSuchElementException();
		}
		return lastUpdate;
	}

	private DeltaUpdate toInitialUpdate(StateContainer container) {
		return new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime(startTime)
			.withTokens(container.getContent())
			.buildUpdate();
	}
	
	private boolean testLastUpdateIsInPeriod() {
		if ( endTime == null || lastUpdate.getTime().isBefore(endTime) ) {
			return true;
		}
		lastUpdate = null;
		return false;
	}

}
