package ru.prolib.aquila.core;

public interface EventProducer {
	
	/**
	 * Suppress events.
	 * <p>
	 * Suppress all outgoing events until restored. All enqueued events will be
	 * cached until {@link #restoreEvents()} call.
	 */
	void suppressEvents();
	
	/**
	 * Restore events.
	 * <p>
	 * Restore all events suppressed by previous {@link #suppressEvents()} call.
	 * Following events will be fired immediately after they are enqueued.
	 */
	void restoreEvents();

}
