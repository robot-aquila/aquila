package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Базовое событие IB.
 * <p>
 * 2012-11-17<br>
 * $Id: IBEvent.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEvent extends EventImpl {

	public IBEvent(EventType type) {
		super(type);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == IBEvent.class ) {
			IBEvent o = (IBEvent) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.isEquals();
		} else {
			return false;
		}
	}

}
