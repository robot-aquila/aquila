package ru.prolib.aquila.core.data;

import java.time.Instant;

import org.threeten.extra.Interval;

/**
 * Time series interface.
 * <p>
 * @param <T> - value type
 */
public interface TSeries<T> extends Series<T> {
	
	T get(Interval interval);

	T get(Instant time);

	TimeFrame getTimeFrame();

}
