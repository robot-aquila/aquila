package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventType;

public interface ObservableSeries<T> extends Series<T> {
	
	/**
	 * Get event type: when the last value of series was changed.
	 * <p>
	 * Events of this type are instances of {@link SeriesEvent} class.
	 * <p>
	 * @return event type
	 */
	public EventType onSet();
	
	/**
	 * Get event type: when the new value was added to series.
	 * <p>
	 * Events of this type are instances of {@link SeriesEvent} class.
	 * <p>
	 * @return event type
	 */
	public EventType onAdd();

}
