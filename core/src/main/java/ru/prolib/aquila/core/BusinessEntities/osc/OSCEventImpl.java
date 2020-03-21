package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Instant;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ContainerEvent;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCEventImpl extends EventImpl implements ContainerEvent {
	protected final ObservableStateContainer container;
	protected final Instant time;
	
	public OSCEventImpl(EventType type, ObservableStateContainer container, Instant time) {
		super(type);
		this.container = container;
		this.time = time;
	}
	
	@Override
	public ObservableStateContainer getContainer() {
		return container;
	}
	@Override
	public Instant getTime() {
		return time;
	}

}