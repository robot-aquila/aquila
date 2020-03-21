package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventImpl;

public class PositionUpdateEvent extends OSCUpdateEventImpl {
	protected final Position position;
	
	public PositionUpdateEvent(EventType type, Position position, Instant time,
			Map<Integer, Object> old_values, Map<Integer, Object> new_values)
	{
		super(type, position, time, old_values, new_values);
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(10087651, 107)
				.append(getType())
				.append(container)
				.append(time)
				.append(oldValues)
				.append(newValues)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionUpdateEvent.class ) {
			return false;
		}
		PositionUpdateEvent o = (PositionUpdateEvent) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.container, container)
				.append(o.time, time)
				.append(o.oldValues, oldValues)
				.append(o.newValues, newValues)
				.build();
	}

}
