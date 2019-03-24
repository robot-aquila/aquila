package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;

public class LengthUpdateEventFactory implements EventFactory {
	private final int prevLength, currLength;
	
	public LengthUpdateEventFactory(int prev_length, int curr_length) {
		this.prevLength = prev_length;
		this.currLength = curr_length;
	}

	@Override
	public Event produceEvent(EventType type) {
		return new LengthUpdateEvent(type, prevLength, currLength);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(8765119, 593)
				.append(prevLength)
				.append(currLength)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != LengthUpdateEventFactory.class ) {
			return false;
		}
		LengthUpdateEventFactory o = (LengthUpdateEventFactory) other;
		return o.prevLength == prevLength
			&& o.currLength == currLength;
	}

}
