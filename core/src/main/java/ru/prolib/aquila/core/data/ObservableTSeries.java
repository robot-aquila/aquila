package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventType;

public interface ObservableTSeries<T> extends TSeries<T>, ObservableSeries {
	
	/**
	 * Get event type: when the series value was updated.
	 * <p>
	 * Events of this type are instances of {@link TSeriesEvent} class.
	 * Be careful processing index of element. It may change between sending and receiving.
	 * <p>
	 * @return event type
	 */
	EventType onUpdate();
	
	/**
	 * Get event type: when the number of elements changed.
	 * <p>
	 * Events of this type are instances of {@link TSeriesEvent} class.
	 * Be careful processing index of element. It may change between sending and receiving.
	 * <p>
	 * @return event type
	 */
	EventType onLengthUpdate();

}
