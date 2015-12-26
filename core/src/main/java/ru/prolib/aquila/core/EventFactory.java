package ru.prolib.aquila.core;

/**
 * Event factory interface.
 */
public interface EventFactory {

	/**
	 * Produce an event for specified event type.
	 * <p>
	 * @param type - event type
	 * @return new event
	 */
	public Event produceEvent(EventType type);

}
