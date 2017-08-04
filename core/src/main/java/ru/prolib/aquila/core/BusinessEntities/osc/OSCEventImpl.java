package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Instant;
import java.util.Set;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ContainerEvent;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCEventImpl extends EventImpl implements ContainerEvent {
	private final ObservableStateContainer container;
	private final Instant time;
	private Set<Integer> updatedTokens;
	
	public OSCEventImpl(EventType type, ObservableStateContainer container, Instant time) {
		super(type);
		this.container = container;
		this.time = time;
	}
	
	public void setUpdatedTokens(Set<Integer> updatedTokens) {
		this.updatedTokens = updatedTokens;
	}
	
	@Override
	public ObservableStateContainer getContainer() {
		return container;
	}

	@Override
	public boolean hasChanged(int token) {
		return updatedTokens != null && updatedTokens.contains(token);
	}

	@Override
	public Set<Integer> getUpdatedTokens() {
		return updatedTokens;
	}

	@Override
	public Instant getTime() {
		return time;
	}

}