package ru.prolib.aquila.core.BusinessEntities;

public enum SchedulerTaskType {
	/**
	 * Identifies a task created by {@link Scheduler#schedule(Runnable, java.time.Instant)}
	 */
	AT_TIME,
	
	/**
	 * Identifies a task created by {@link Scheduler#schedule(Runnable, java.time.Instant, long)}
	 */
	AT_TIME_PERIODIC,
	
	/**
	 * Identifies a task created by {@link Scheduler#schedule(Runnable, long)}
	 */
	WITH_DELAY,
	
	/**
	 * Identifies a task created by {@link Scheduler#schedule(Runnable, long, long)}
	 */
	WITH_DELAY_PERIODIC,

	/**
	 * Identifies a task created by {@link Scheduler#scheduleAtFixedRate(Runnable, java.time.Instant, long)}
	 */
	AT_TIME_FIXEDRATE,
	
	/**
	 * Identifies a task created by {@link Scheduler#scheduleAtFixedRate(Runnable, long, long)}
	 */
	WITH_DELAY_FIXEDRATE

}
