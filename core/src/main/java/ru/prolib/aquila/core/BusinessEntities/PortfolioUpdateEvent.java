package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCUpdateEventImpl;

public class PortfolioUpdateEvent extends OSCUpdateEventImpl {
	protected final Portfolio portfolio;

	public PortfolioUpdateEvent(EventType type, Portfolio portfolio, Instant time,
			Map<Integer, Object> old_values, Map<Integer, Object> new_values)
	{
		super(type, portfolio, time, old_values, new_values);
		this.portfolio = portfolio;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(776541273, 921)
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
		if ( other == null || other.getClass() != PortfolioUpdateEvent.class ) {
			return false;
		}
		PortfolioUpdateEvent o = (PortfolioUpdateEvent) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.container, container)
				.append(o.time, time)
				.append(o.oldValues, oldValues)
				.append(o.newValues, newValues)
				.build();
	}
	
}
