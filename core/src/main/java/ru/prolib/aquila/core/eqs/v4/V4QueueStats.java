package ru.prolib.aquila.core.eqs.v4;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.EventQueueStats;

public class V4QueueStats implements EventQueueStats {
	private final long enqueued, sent, dispatched, preparingTime, dispatchingTime, deliveryTime;
	
	public V4QueueStats(long enqueued,
			long sent,
			long dispatched,
			long preparing_time,
			long dispatching_time,
			long delivery_time)
	{
		this.enqueued = enqueued;
		this.sent = sent;
		this.dispatched = dispatched;
		this.preparingTime = preparing_time;
		this.dispatchingTime = dispatching_time;
		this.deliveryTime = delivery_time;
	}

	@Override
	public long getPreparingTime() {
		return preparingTime;
	}

	@Override
	public long getDispatchingTime() {
		return dispatchingTime;
	}

	@Override
	public long getDeliveryTime() {
		return deliveryTime;
	}

	@Override
	public long getTotalEventsSent() {
		return sent;
	}

	@Override
	public long getTotalEventsDispatched() {
		return dispatched;
	}

	@Override
	public long getTotalEventsEnqueued() {
		return enqueued;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(99016125, 761)
				.append(enqueued)
				.append(sent)
				.append(dispatched)
				.append(preparingTime)
				.append(dispatchingTime)
				.append(deliveryTime)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != V4QueueStats.class ) {
			return false;
		}
		V4QueueStats o = (V4QueueStats) other;
		return new EqualsBuilder()
				.append(o.enqueued, enqueued)
				.append(o.sent, sent)
				.append(o.dispatched, dispatched)
				.append(o.preparingTime, preparingTime)
				.append(o.dispatchingTime, dispatchingTime)
				.append(o.deliveryTime, deliveryTime)
				.build();
	}

}
