package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCEventFactory implements EventFactory {
	private final ObservableStateContainer container;
	private final Set<Integer> updatedTokens;
	private final Instant time;
	
	public OSCEventFactory(ObservableStateContainer container, Instant time) {
		super();
		this.container = container;
		this.updatedTokens = new HashSet<>(container.getUpdatedTokens());
		this.time = time;
	}

	@Override
	public Event produceEvent(EventType type) {
		OSCEventImpl e = new OSCEventImpl(type, container, time);
		e.setUpdatedTokens(updatedTokens);
		return e;
	}
	
}