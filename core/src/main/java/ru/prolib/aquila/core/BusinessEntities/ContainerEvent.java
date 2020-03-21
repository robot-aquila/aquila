package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

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
	
	/**
	 * Get time of event.
	 * <p>
	 * @return time when this event was generated
	 */
	public Instant getTime();
		
}
