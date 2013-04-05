package ru.prolib.aquila.ib.event;

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

}
