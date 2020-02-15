package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class LengthUpdateEvent extends EventImpl {
	protected final int prevLength, currLength;

	public LengthUpdateEvent(EventType type, int prevLength, int currLength) {
		super(type);
		this.prevLength = prevLength;
		this.currLength = currLength;
	}
	
	public int getPrevLength() {
		return prevLength;
	}
	
	public int getCurrLength() {
		return currLength;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != LengthUpdateEvent.class ) {
			return false;
		}
		LengthUpdateEvent o = (LengthUpdateEvent) other;
		return o.getType() == getType()
			&& o.prevLength == prevLength
			&& o.currLength == currLength;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(878614327, 9031)
				.append(getType())
				.append(prevLength)
				.append(currLength)
				.build();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(getType().getId())
				.append(".LengthUpdate[").append(prevLength).append("->").append(currLength).append("]").toString();
	}

}
