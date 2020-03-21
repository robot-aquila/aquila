package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventImpl;

public class SecurityUpdateEvent extends OSCUpdateEventImpl {
	protected final Security security;

	public SecurityUpdateEvent(EventType type, Security security, Instant time,
			Map<Integer, Object> old_values, Map<Integer, Object> new_values)
	{
		super(type, security, time, old_values, new_values);
		this.security = security;
	}
	
	public Security getSecurity() {
		return security;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityUpdateEvent.class ) {
			return false;
		}
		SecurityUpdateEvent o = (SecurityUpdateEvent) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.container, container)
				.append(o.time, time)
				.append(o.oldValues, oldValues)
				.append(o.newValues, newValues)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(887751231, 813)
				.append(getType())
				.append(container)
				.append(time)
				.append(oldValues)
				.append(newValues)
				.build();
	}

}
