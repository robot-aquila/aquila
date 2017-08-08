package ru.prolib.aquila.probe;

import java.time.Instant;

public interface ThreadSynchronizer {
	
	/**
	 * Called before a time slot execution.
	 * <p>
	 * @param currentTime - current scheduler time
	 */
	void beforeExecution(Instant currentTime);
	
	/**
	 * Called after a time slot execution.
	 * <p>
	 * @param currentTime - current scheduler time
	 */
	void afterExecution(Instant currentTime);
	
	/**
	 * Wait while controlled thread is in well state with current time.
	 * <p>
	 * @param currentTime - current scheduler time
	 * @throws InterruptedException - operation was interrupted
	 */
	void waitForThread(Instant currentTime) throws InterruptedException;

}
