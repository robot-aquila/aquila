package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.Event;

/**
 * Container update event.
 */
public interface ContainerEvent extends Event {
	
	/**
	 * Get container instance.
	 * <p>
	 * @return container
	 */
	public Container getContainer();
		
}
