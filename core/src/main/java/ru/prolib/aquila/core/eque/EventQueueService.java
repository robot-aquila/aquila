package ru.prolib.aquila.core.eque;

import ru.prolib.aquila.core.EventQueueStats;
import ru.prolib.aquila.core.FlushIndicator;

public interface EventQueueService {
	
	/**
	 * Create indicator of event queue emptiness.
	 * <p>
	 * @return new indicator
	 */
	FlushIndicator createIndicator();

	/**
	 * Get event queue statistics.
	 * <p>
	 * @return queue statistics
	 */
	EventQueueStats getStats();
	
	/**
	 * Indicates that event has been passed to event queue for processing.
	 * Note that the event is not actually enqueued. It's called before
	 * possible blocking call putting an event to queue.
	 */
	void eventEnqueued();
	
	/**
	 * Indicates that event has been actually put into processing queue
	 * and scheduled for dispatching.
	 */
	void eventSent();
	
	/**
	 * Indicates that event has been dispatched to every awaited listener.
	 */
	void eventDispatched();
	
	
	void addPreparingTime(long nanos);
	
	void addDispatchingTime(long nanos);
	
	void addDeliveryTime(long nanos);

	void shutdown();

}
