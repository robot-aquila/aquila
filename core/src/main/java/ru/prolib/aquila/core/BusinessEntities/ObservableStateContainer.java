package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Observable and thread-safe data container.
 * <p>
 * The data container gives an access to a set of values which identified by
 * numeric token ID. Container allows track when and which tokens changed.
 */
public interface ObservableStateContainer extends UpdatableStateContainer {
	
	/**
	 * When container is available for reading.
	 * <p>
	 * Container availability is the state when the object contains minimal data
	 * to make this container available to normal work. The set of required
	 * tokens depends on container specification. <s>The first update of every
	 * container is an availability event.</s> Usually  a collection of containers
	 * forwards all container events to consumer. For such cases the event of
	 * availability may be used one-time catching of the container when it
	 * arrived to the environment. See concrete collection to get more details
	 * about usage of availability events.
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
	
	/**
	 * When container is closed.
	 * <p>
	 * Allows catching events {@link ContainerEvent}.
	 * <p>
	 * @return event type
	 */
	public EventType onClose();

	public boolean isAvailable();
	
}
