package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.concurrency.Lockable;

public interface SchedulerTask extends TaskHandler, Lockable {

	/**
	 * Test that the task is periodic.
	 * <p>
	 * @return true if the task is periodic, false otherwise
	 */
	public abstract boolean isPeriodic();

	/**
	 * Test that the task is scheduled for execution.
	 * <p>
	 * @return
	 */
	public abstract boolean isScheduled();

	/**
	 * Get current state of the task.
	 * <p>
	 * @return the task state
	 */
	public abstract SchedulerTaskState getState();

	/**
	 * Get period of the task.
	 * <p>
	 * @return period of the task or zero if the task is not periodic
	 */
	public abstract long getPeriod();

	/**
	 * Get runnable instance of the task.
	 * <p>
	 * @return the runnable
	 */
	public abstract Runnable getRunnable();

	/**
	 * Get next execution time.
	 * <p>
	 * @return - the next execution time 
	 */
	public abstract Instant getNextExecutionTime();

}