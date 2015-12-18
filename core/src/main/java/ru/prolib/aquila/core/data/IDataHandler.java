package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.IObservable;

/**
 * Data handler interface.
 * <p>
 * This interface declares an access to attributes and methods of a separate data stream.
 */
public interface IDataHandler extends IObservable {
	
	/**
	 * Get current handler state.
	 * <p>
	 * @return handler state
	 */
	public DataHandlerState getState();
	
	/**
	 * Get text descriptor of the handler.
	 * <p>
	 * @return descriptor
	 */
	public String getDescriptor();
	
	/**
	 * Close the handler and free all acquired resources.
	 */
	public void close();

	/**
	 * Lock this object for reading or changing its state.
	 */
	public void lock();
	
	/**
	 * Release lock.
	 */
	public void unlock();

}