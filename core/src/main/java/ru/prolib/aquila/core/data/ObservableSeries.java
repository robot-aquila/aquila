package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.EventType;

/**
 * Intermediate interface to reduce coupling with time series.
 */
public interface ObservableSeries {

	EventType onUpdate();
	EventType onLengthUpdate();
	
}
