package ru.prolib.aquila.ib.utils;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class IBEventHistoricalData extends EventImpl {

	private IBHistoricalRow row;
	/**
	 * @param type
	 */	
	public IBEventHistoricalData(EventType type, IBHistoricalRow row) {
		super(type);
		this.row = row;
	}
	
	public IBHistoricalRow getRow() {
		return row;
	}

}
