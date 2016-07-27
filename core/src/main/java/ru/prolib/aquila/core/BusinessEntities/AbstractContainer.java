package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.concurrency.Lockable;

public interface AbstractContainer extends Lockable {

	/**
	 * Get container ID.
	 * <p>
	 * @return container ID
	 */
	public String getContainerID();
	
	/**
	 * Lock container.
	 */
	@Override
	public void lock();
	
	/**
	 * Unlock container.
	 */
	@Override
	public void unlock();
	
	/**
	 * Close container.
	 */
	public void close();
	
	/**
	 * Test container is closed.
	 * <p>
	 * @return true if closed, false otherwise
	 */
	public boolean isClosed();
	
}
