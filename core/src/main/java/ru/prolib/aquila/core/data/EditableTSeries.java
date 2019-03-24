package ru.prolib.aquila.core.data;

import java.time.Instant;

public interface EditableTSeries<T> extends TSeries<T> {
	
	TSeriesUpdate set(Instant time, T value);
	
	void clear();
	
	/**
	 * Truncate series down to specified length.
	 * <p>
	 * @param length - new length. Zero or positive.
	 */
	void truncate(int length);

}
