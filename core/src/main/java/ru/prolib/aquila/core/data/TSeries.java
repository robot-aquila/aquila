package ru.prolib.aquila.core.data;

import java.time.Instant;

/**
 * Time series interface.
 * <p>
 * @param <T> - value type
 */
public interface TSeries<T> extends KSeries<Instant, T> {

	ZTFrame getTimeFrame();
	
	/**
	 * Get first element earlier than specified time.
	 * <p>
	 * @param time - time to search element (exclusive) 
	 * @return first element before the time or null if no elements before time 
	 */
	T getFirstBefore(Instant time);
	
	/**
	 * Get index of first element earlier than specified time.
	 * <p>
	 * @param time - time to search element index (exclusive)
	 * @return index of element or -1 if no elements found
	 */
	int getFirstIndexBefore(Instant time);

}
