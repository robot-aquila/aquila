package ru.prolib.aquila.core.eqs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CmdAddTime extends Cmd {
	protected final Long preparing, dispatching, delivery;

	public CmdAddTime(Long preparing, Long dispatching, Long delivery) {
		super(CmdType.ADD_TIME);
		this.preparing = preparing;
		this.dispatching = dispatching;
		this.delivery = delivery;
	}
	
	public Long getPreparing() {
		return preparing;
	}
	
	public Long getDispatching() {
		return dispatching;
	}
	
	public Long getDelivery() {
		return delivery;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7915633, 71)
				.append(preparing)
				.append(dispatching)
				.append(delivery)
				.build();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName())
				.append("[preparing=").append(preparing)
				.append(",dispatching=").append(dispatching)
				.append(",delivery=").append(delivery)
				.append("]")
				.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdAddTime.class ) {
			return false;
		}
		CmdAddTime o = (CmdAddTime) other;
		return new EqualsBuilder()
				.append(o.preparing, preparing)
				.append(o.dispatching, dispatching)
				.append(o.delivery, delivery)
				.build();
	}

}
