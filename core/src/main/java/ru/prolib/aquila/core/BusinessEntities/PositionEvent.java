package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.ContainerImpl.ContainerEventImpl;

/**
 * Event related to market position.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionEvent.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionEvent extends ContainerEventImpl {
	private final Position position;

	public PositionEvent(EventType type, Position position) {
		super(type, position);
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

}
