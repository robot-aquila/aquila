package ru.prolib.aquila.core.data;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.Event;

public interface TSeriesEvent<T> extends Event {

	/**
	 * Check that new interval was created.
	 * <p>
	 * @return true if new interval was added to the series, false otherwise
	 */
	boolean isNewInterval();
	
	/**
	 * Get old (previous) value.
	 * <p>
	 * @return value before update
	 */
	T getOldValue();
	
	/**
	 * Get new (updated) value.
	 * <p>
	 * @return value after update
	 */
	T getNewValue();
	
	/**
	 * Get index of updated element.
	 * <p>
	 * @return actual index of element on event generation time
	 */
	int getIndex();
	
	/**
	 * Get updated interval.
	 * <p>
	 * @return interval
	 */
	Interval getInterval();

}
