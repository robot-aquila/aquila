package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

/**
 * Interface of a self-planned runnable.
 */
public interface SPRunnable extends Runnable {
	
	/**
	 * Get time of the next execution.
	 * <p>
	 * @param currentTime - current time to determine the next execution time
	 * @return the next execution time or null if the task must be cancelled
	 */
	public Instant getNextExecutionTime(Instant currentTime);
	
	/**
	 * Test that this task is a long-term task.
	 * <p>
	 * This method is used to determine how much time the task may consume in
	 * total. If there are only few and fast operations which will not ask to
	 * much time of execution then the task is a short-time. If the task is
	 * linked with IO or with analysis of huge amount of data then the task
	 * potentially is a long-term task and should be executed in a separate
	 * thread. This is a best place to determine this fact because only the task
	 * implementation should know everything about itself. That is not an issue
	 * of usage of scheduler or the task consumer.
	 * <p>
	 * @return true if this task is a long-term task, false otherwise
	 */
	public boolean isLongTermTask();

}
