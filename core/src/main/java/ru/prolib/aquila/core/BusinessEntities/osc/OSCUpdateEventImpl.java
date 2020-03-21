package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ContainerUpdateEvent;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCUpdateEventImpl extends OSCEventImpl implements ContainerUpdateEvent {
	protected final Map<Integer, Object> oldValues, newValues;

	public OSCUpdateEventImpl(EventType type, ObservableStateContainer container, Instant time,
			Map<Integer, Object> old_values, Map<Integer, Object> new_values)
	{
		super(type, container, time);
		this.oldValues = old_values;
		this.newValues = new_values;
	}

	@Override
	public boolean hasChanged(int token) {
		return newValues.containsKey(token);
	}

	@Override
	public Set<Integer> getUpdatedTokens() {
		return newValues.keySet();
	}

	@Override
	public Map<Integer, Object> getOldValues() {
		return oldValues;
	}

	@Override
	public Map<Integer, Object> getNewValues() {
		return newValues;
	}

}
