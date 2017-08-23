package ru.prolib.aquila.core.data;

import java.time.Instant;

/**
 * Time series interface.
 * <p>
 * @param <T> - value type
 */
public interface TSeries<T> extends Series<T> {

	T get(Instant time);

	TimeFrame getTimeFrame();

}
