package ru.prolib.aquila.core.data;

import java.time.Instant;

public interface EditableTSeries<T> extends TSeries<T> {
	
	void set(Instant time, T value);
	
	void clear();

}
