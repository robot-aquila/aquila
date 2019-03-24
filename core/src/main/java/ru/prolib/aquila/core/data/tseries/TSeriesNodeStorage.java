package ru.prolib.aquila.core.data.tseries;

import java.time.Instant;

import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;

public interface TSeriesNodeStorage extends Lockable {

	/**
	 * Get timeframe.
	 * <p>
	 * @return timeframe of the series
	 */
	ZTFrame getTimeFrame();

	/**
	 * Register new series.
	 * <p>
	 * @return new series ID
	 */
	int registerSeries();

	/**
	 * Set value.
	 * <p>
	 * @param time - the time is used to determine interval
	 * @param seriesID - series identifier
	 * @param value - value to set
	 * @return information of an update
	 */
	TSeriesUpdate setValue(Instant time, int seriesID, Object value);

	/**
	 * Get value by timestamp.
	 * <p>
	 * @param time - timestamp
	 * @param seriesID - series identifier
	 * @return value or null if has no value associated with the time
	 */
	Object getValue(Instant time, int seriesID);

	/**
	 * Get value by index.
	 * <p>
	 * @param index - index in series
	 * @param seriesID - series identifier
	 * @return value
	 * @throws ValueOutOfRangeException - index is out of range
	 * @throws ValueException - an error occurred
	 */
	Object getValue(int index, int seriesID) throws ValueException;

	/**
	 * Get last value.
	 * <p>
	 * @param seriesID - series identifier
	 * @return last value or null if no values in series
	 */
	Object getValue(int seriesID);

	/**
	 * Remove all elements.
	 */
	void clear();

	/**
	 * Get number of elements in series.
	 * <p>
	 * @return number of elements
	 */
	int getLength();
	
	/**
	 * Get existing interval start time.
	 * <p>
	 * @param index - index of interval
	 * @return interval start time
	 * @throws ValueOutOfRangeException - index is out of range
	 * @throws ValueException - an error occurred
	 */
	Instant getIntervalStart(int index) throws ValueException;

	/**
	 * Get existing interval index.
	 * <p>
	 * @param time - time inside interval
	 * @return interval index or -1 if interval of the time is not exists
	 */
	int getIntervalIndex(Instant time);
	
	/**
	 * Truncate series down to specified length.
	 * <p>
	 * @param length - new length. Zero or positive.
	 */
	void truncate(int length);
	
}
