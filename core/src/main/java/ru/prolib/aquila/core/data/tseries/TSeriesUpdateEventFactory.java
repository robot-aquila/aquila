package ru.prolib.aquila.core.data.tseries;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.TSeriesUpdate;

public class TSeriesUpdateEventFactory implements EventFactory {
	private final TSeriesUpdate update;
	
	public TSeriesUpdateEventFactory(TSeriesUpdate update) {
		this.update = update;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Event produceEvent(EventType type) {
		return new TSeriesEventImpl(type, update);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSeriesUpdateEventFactory.class ) {
			return false;
		}
		TSeriesUpdateEventFactory o = (TSeriesUpdateEventFactory) other;
		return new EqualsBuilder()
				.append(update, o.update)
				.isEquals();
	}
	
}