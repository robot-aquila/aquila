package ru.prolib.aquila.core.concurrency;

public interface Lockable {
	
	/**
	 * Get lockable ID.
	 * <p>
	 * @return lockable ID
	 */
	public LID getLID();
	
	/**
	 * Lock object for current thread.
	 */
	public void lock();
	
	/**
	 * Unlock object.
	 */
	public void unlock();

}
