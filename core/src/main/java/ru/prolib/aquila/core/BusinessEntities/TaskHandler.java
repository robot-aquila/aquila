package ru.prolib.aquila.core.BusinessEntities;

/**
 * Interface of a scheduler task.
 */
public interface TaskHandler {
	
	/**
	 * Cancel the task.
	 * <p>
	 * @return true if this task is scheduled for one-time execution and has not
	 * yet run, or this task is scheduled for repeated execution. Returns false
	 * if the task was scheduled for one-time execution and has already run, or
	 * if the task was never scheduled, or if the task was already cancelled.
	 * (Loosely speaking, this method returns true if it prevents one or more
	 * scheduled executions from taking place.)
	 */
	public boolean cancel();

}
