package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;

/**
 * Таймер, основанный на локальном времени.
 */
public class TimerLocal implements Timer {
	
	public TimerLocal() {
		super();
	}

	@Override
	public Date getCurrentTime() {
		return new Date();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == TimerLocal.class;
	}

}
