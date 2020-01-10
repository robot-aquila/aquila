package ru.prolib.aquila.core.eqs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CmdAddCount extends Cmd {
	protected final Long enqueued, sent, dispatched;

	public CmdAddCount(Long enqueued, Long sent, Long dispatched) {
		super(CmdType.ADD_COUNT);
		this.enqueued = enqueued;
		this.sent = sent;
		this.dispatched = dispatched;
	}
	
	public Long getEnqueued() {
		return enqueued;
	}
	
	public Long getSent() {
		return sent;
	}
	
	public Long getDispatched() {
		return dispatched;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1152009, 37)
				.append(enqueued)
				.append(sent)
				.append(dispatched)
				.build();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName())
				.append("[enqueued=").append(enqueued)
				.append(",sent=").append(sent)
				.append(",dispatched=").append(dispatched)
				.append("]")
				.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdAddCount.class ) {
			return false;
		}
		CmdAddCount o = (CmdAddCount) other;
		return new EqualsBuilder()
				.append(o.enqueued, enqueued)
				.append(o.sent, sent)
				.append(o.dispatched, dispatched)
				.build();
	}

}
