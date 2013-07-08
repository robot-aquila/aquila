package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;

/**
 * Стандартный планировщик задач.
 */
public class SchedulerLocal implements Scheduler {
	
	public SchedulerLocal() {
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
		return other != null && other.getClass() == SchedulerLocal.class;
	}

}
