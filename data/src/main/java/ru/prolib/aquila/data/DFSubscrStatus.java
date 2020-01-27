package ru.prolib.aquila.data;

/**
 * Data feed subscription status.
 */
public enum DFSubscrStatus {
	
	/**
	 * Not subscribed to data feed.
	 */
	NOT_SUBSCR,
	
	/**
	 * Subscription request received.
	 */
	PENDING_SUBSCR,

	/**
	 * Subscribed to data feed.
	 */
	SUBSCR,

	/**
	 * Unsubscription request received.
	 */
	PENDING_UNSUBSCR,
	
	/**
	 * Unable to subscribe because feed is not available.
	 */
	NOT_AVAILABLE
	
}
