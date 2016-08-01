package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * The task class of the stub scheduler.
 */
public class SchedulerStubTask implements TaskHandler, Comparable<SchedulerStubTask>, TStamped {
	private final SchedulerTaskType type;
	private final Instant time;
	private final Long delay, period;
	private final Runnable runnable;
	private boolean cancelled = false;
	
	/**
	 * Create a task handler of execution at the specified time.
	 * <p>
	 * @param scheduledTime - the time of execution
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask atTime(Instant scheduledTime, Runnable runnable) {
		return new SchedulerStubTask(SchedulerTaskType.AT_TIME,
				scheduledTime, null, null, runnable);
	}
	
	/**
	 * Create a task handler of execution at the specified time.
	 * <p>
	 * @param scheduledTime - the string of time of execution in a
	 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT} format
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask atTime(String scheduledTime, Runnable runnable) {
		return atTime(Instant.parse(scheduledTime), runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-delay execution beginning at the specified time.
	 * <p>
	 * @param scheduledTime - the time of the first execution
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask atTimePeriodic(Instant scheduledTime,
			Long period, Runnable runnable)
	{
		return new SchedulerStubTask(SchedulerTaskType.AT_TIME_PERIODIC,
				scheduledTime, null, period, runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-delay execution beginning at the specified time.
	 * <p>
	 * @param scheduledTime - the string of time of the first execution in a
	 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT} format
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask atTimePeriodic(String scheduledTime,
			Long period, Runnable runnable)
	{
		return atTimePeriodic(Instant.parse(scheduledTime), period, runnable);
	}
	
	/**
	 * Create a task handler of execution after the specified delay.
	 * <p>
	 * @param scheduledTime - the time of execution
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask withDelay(Instant scheduledTime, Long delay,
			Runnable runnable)
	{
		return new SchedulerStubTask(SchedulerTaskType.WITH_DELAY,
				scheduledTime, delay, null, runnable);
	}
	
	/**
	 * Create a task handler of execution after the specified delay.
	 * <p>
	 * @param scheduledTime - the string of time of execution in a
	 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT} format
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask withDelay(String scheduledTime, Long delay,
			Runnable runnable)
	{
		return withDelay(Instant.parse(scheduledTime), delay, runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-delay execution beginning after the specified delay.
	 * <p>
	 * @param scheduledTime - the time of execution
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask withDelayPeriodic(Instant scheduledTime,
			Long delay, Long period, Runnable runnable)
	{
		return new SchedulerStubTask(SchedulerTaskType.WITH_DELAY_PERIODIC,
				scheduledTime, delay, period, runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-delay execution beginning after the specified delay.
	 * <p>
	 * @param scheduledTime - the string of time of execution in a
	 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT} format
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask withDelayPeriodic(String scheduledTime,
			Long delay, Long period, Runnable runnable)
	{
		return withDelayPeriodic(Instant.parse(scheduledTime), delay, period, runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-rate execution beginning at the specified time.
	 * <p>
	 * @param scheduledTime - the time of execution
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask atTimeFixedRate(Instant scheduledTime,
			Long period, Runnable runnable)
	{
		return new SchedulerStubTask(SchedulerTaskType.AT_TIME_FIXEDRATE,
				scheduledTime, null, period, runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-rate execution beginning at the specified time.
	 * <p>
	 * @param scheduledTime - the string of time of execution in a
	 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT} format
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask atTimeFixedRate(String scheduledTime,
			Long period, Runnable runnable)
	{
		return atTimeFixedRate(Instant.parse(scheduledTime), period, runnable);
	}	
	
	/**
	 * Create a task handler of repeated fixed-rate execution beginning after the specified delay.
	 * <p>
	 * @param scheduledTime - the time of execution
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask withDelayFixedRate(Instant scheduledTime,
			Long delay, Long period, Runnable runnable)
	{
		return new SchedulerStubTask(SchedulerTaskType.WITH_DELAY_FIXEDRATE,
				scheduledTime, delay, period, runnable);
	}
	
	/**
	 * Create a task handler of repeated fixed-rate execution beginning after the specified delay.
	 * <p>
	 * @param scheduledTime - the string of time of execution in a
	 * {@link java.time.format.DateTimeFormatter#ISO_INSTANT} format
	 * @param delay - delay in milliseconds before task is to be executed
	 * @param period - time in milliseconds between the task executions
	 * @param runnable - the runnable instance
	 * @return the task handler
	 */
	public static SchedulerStubTask withDelayFixedRate(String scheduledTime,
			Long delay, Long period, Runnable runnable)
	{
		return withDelayFixedRate(Instant.parse(scheduledTime), delay, period, runnable);
	}
	
	public SchedulerStubTask(SchedulerTaskType type, Instant scheduledTime,
			Long delay, Long period, Runnable runnable)
	{
		this.type = type;
		this.time = scheduledTime;
		this.delay = delay;
		this.period = period;
		this.runnable = runnable;
	}
	
	/**
	 * Get time of the first task execution.
	 * <p>
	 * @return the time of execution
	 */
	@Override
	public Instant getTime() {
		return time;
	}
	
	public SchedulerTaskType getType() {
		return type;
	}
	
	public Long getDelay() {
		return delay;
	}
	
	public Long getPeriod() {
		return period;
	}
	
	public Runnable getRunnable() {
		return runnable;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public int compareTo(SchedulerStubTask o) {
		int r = time.compareTo(o.time);
		if ( r < 0 ) {
			r = -1;
		} else if ( r > 0 ) {
			r = 1;
		}
		return r;
	}

	@Override
	public boolean cancel() {
		if ( cancelled == false ) {
			return cancelled = true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SchedulerStubTask.class ) {
			return false;
		}
		SchedulerStubTask o = (SchedulerStubTask) other;
		return new EqualsBuilder()
			.append(type, o.type)
			.append(time, o.time)
			.append(delay, o.delay)
			.append(period, o.period)
			.append(runnable, o.runnable)
			.append(cancelled, o.cancelled)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + type + "@" + time + "["
				+ (delay == null ? "" : "D:" + delay + " ")
				+ (period == null ? "" : "P:" + period + " ")
				+ runnable + "]";
	}
	
}
