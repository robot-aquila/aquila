package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Instant;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCEventFactory implements EventFactory {
	protected final ObservableStateContainer container;
	protected final Instant time;
	
	public OSCEventFactory(ObservableStateContainer container, Instant time) {
		super();
		this.container = container;
		this.time = time;
	}

	@Override
	public Event produceEvent(EventType type) {
		return new OSCEventImpl(type, container, time);
	}
	
}