package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.Set;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ContainerEvent;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCEventImpl extends EventImpl implements ContainerEvent {
	private final ObservableStateContainer container;
	private Set<Integer> updatedTokens;
	
	public OSCEventImpl(EventType type, ObservableStateContainer container) {
		super(type);
		this.container = container;
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

}