package ru.prolib.aquila.core.data;

import org.threeten.extra.Interval;

public interface TSeriesUpdate {

	/**
	 * Check new node was created.
	 * <p>
	 * @return true if new node associated with interval was created in result of operation, false otherwise.
	 */
	boolean isNewNode();

	/**
	 * Check that value was changed.
	 * <p>
	 * @return true if value was changed in result of operation, false otherwise.
	 */
	boolean hasChanged();

	/**
	 * Get interval.
	 * <p>
	 * @return interval associated with the update
	 */
	Interval getInterval();

	/**
	 * Get node index.
	 * <p>
	 * @return index of node. Note: the index may changed in result of adding new nodes. Use with caution.
	 */
	int getNodeIndex();

	/**
	 * Get old (previous) value.
	 * <p>
	 * @return value before update
	 */
	Object getOldValue();

	/**
	 * Get new (updated) value.
	 * <p>
	 * @return value after update
	 */
	Object getNewValue();

}