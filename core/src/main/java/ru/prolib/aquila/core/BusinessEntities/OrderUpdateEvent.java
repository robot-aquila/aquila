package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventImpl;

public class OrderUpdateEvent extends OSCUpdateEventImpl {
	protected final Order order;
	
	public OrderUpdateEvent(EventType type, Order order, Instant time,
			Map<Integer, Object> old_values, Map<Integer, Object> new_values)
	{
		super(type, order, time, old_values, new_values);
		this.order = order;
	}
	
	public Order getOrder() {
		return order;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(11776551, 61)
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
		if ( other == null || other.getClass() != OrderUpdateEvent.class ) {
			return false;
		}
		OrderUpdateEvent o = (OrderUpdateEvent) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.container, container)
				.append(o.time, time)
				.append(o.oldValues, oldValues)
				.append(o.newValues, newValues)
				.build();
	}

}
