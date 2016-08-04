package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Observable and thread-safe data container.
 * <p>
 * The data container gives an access to a set of values which identified by
 * numeric token ID. Container allows track when and which tokens changed.
 */
public interface ObservableStateContainer extends StateContainer {
	
	/**
	 * When container is available for reading.
	 * <p>
	 * @return event type
	 */
	public EventType onAvailable();

	/**
	 * When container updated.
	 * <p>
	 * Allows catching events {@link ContainerEvent}.
	 * <p>
	 * @return event type
	 */
	public EventType onUpdate();

	public boolean isAvailable();
	
}
