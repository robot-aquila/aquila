package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;

/**
 * Event related to market position.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionEvent.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionEvent extends OSCEventImpl {
	protected final Position position;

	public PositionEvent(EventType type, Position position, Instant time) {
		super(type, position, time);
		this.position = position;
	}
	
	/**
	 * Get position instance.
	 * <p>
	 * @return position instance
	 */
	public Position getPosition() {
		return position;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionEvent.class ) {
			return false;
		}
		PositionEvent o = (PositionEvent) other;
		return o.getType() == getType()
			&& o.position == position
			&& new EqualsBuilder()
				.append(o.time, time)
				.isEquals();
	}

}
