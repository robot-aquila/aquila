package ru.prolib.aquila.core.data.tseries;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.TSeriesEvent;
import ru.prolib.aquila.core.data.TSeriesUpdate;

public class TSeriesEventImpl<T> extends EventImpl implements TSeriesEvent<T> {
	private final TSeriesUpdate update;

	public TSeriesEventImpl(EventType type, TSeriesUpdate update) {
		super(type);
		this.update = update;
	}
	
	public TSeriesUpdate getUpdate() {
		return update;
	}

	@Override
	public boolean isNewInterval() {
		return update.isNewNode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getOldValue() {
		return (T) update.getOldValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNewValue() {
		return (T) update.getNewValue();
	}

	@Override
	public int getIndex() {
		return update.getNodeIndex();
	}

	@Override
	public Interval getInterval() {
		return update.getInterval();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSeriesEventImpl.class ) {
			return false;
		}
		TSeriesEventImpl o = (TSeriesEventImpl) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.update, update)
				.isEquals();
	}

}
