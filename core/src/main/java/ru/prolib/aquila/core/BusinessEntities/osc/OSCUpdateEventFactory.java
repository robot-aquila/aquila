package ru.prolib.aquila.core.BusinessEntities.osc;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;

public class OSCUpdateEventFactory extends OSCEventFactory {
	protected final Map<Integer, Object> oldValues, newValues;
	
	public OSCUpdateEventFactory(ObservableStateContainer container,
			Instant time,
			Map<Integer, Object> old_values,
			Map<Integer, Object> new_values)
	{
		super(container, time);
		this.oldValues = Collections.unmodifiableMap(old_values);
		this.newValues = Collections.unmodifiableMap(new_values);
	}

	@Override
	public Event produceEvent(EventType type) {
		return new OSCUpdateEventImpl(type, container, time, oldValues, newValues);
	}

}
