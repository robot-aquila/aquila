package ru.prolib.aquila.ib.utils;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class IBRespHandledEvent extends EventImpl {

	private String lastDate;
	/**
	 * @param type
	 */
	public IBRespHandledEvent(EventType type, String lastDate) {
		super(type);
		this.lastDate = lastDate;
	}
	
	public String getLastDate() {
		return lastDate;
	}

}
