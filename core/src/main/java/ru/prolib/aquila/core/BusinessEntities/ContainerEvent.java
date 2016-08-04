package ru.prolib.aquila.core.BusinessEntities;

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
	public ObservableStateContainer getContainer();
		
}
