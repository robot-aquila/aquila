package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class SeriesEvent<T> extends EventImpl {
	private final int index;
	private final T value;

	public SeriesEvent(EventType type, int index, T value) {
		super(type);
		this.index = index;
		this.value = value;
	}
	
	public int getIndex() {
		return index;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getType().getId() + "@" + index + " " + value + "]";
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SeriesEvent.class ) {
			return false;
		}
		SeriesEvent o = (SeriesEvent) other;
		return new EqualsBuilder()
			.appendSuper(o.getType() == getType())
			.append(o.index, index)
			.append(o.value, value)
			.isEquals();
	}
	
}
