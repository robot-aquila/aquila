package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCEventFactory implements EventFactory {
	private final ObservableStateContainer container;
	private final Set<Integer> updatedTokens;
	
	public OSCEventFactory(ObservableStateContainer container) {
		super();
		this.container = container;
		this.updatedTokens = new HashSet<>(container.getUpdatedTokens());
	}

	@Override
	public Event produceEvent(EventType type) {
		OSCEventImpl e = new OSCEventImpl(type, container);
		e.setUpdatedTokens(updatedTokens);
		return e;
	}
	
}