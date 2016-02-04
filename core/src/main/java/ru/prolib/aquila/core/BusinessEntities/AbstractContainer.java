package ru.prolib.aquila.core.BusinessEntities;

public interface AbstractContainer {

	/**
	 * Get container ID.
	 * <p>
	 * @return container ID
	 */
	public String getContainerID();
	
	/**
	 * Lock container.
	 */
	public void lock();
	
	/**
	 * Unlock container.
	 */
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
