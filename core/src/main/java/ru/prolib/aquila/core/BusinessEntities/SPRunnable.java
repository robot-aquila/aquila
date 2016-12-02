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

}
