package ru.prolib.aquila.qforts.impl;

public enum QFOrderExecutionTriggerMode {
	 
	/**
	 * Reactor subscribes for LastTrade events of terminal. Each event is used
	 * to determine available liquidity and triggers execution of active orders.
	 */
	USE_LAST_TRADE_EVENT_OF_SECURITY,
	
	/**
	 * Reactor acts as L1 update consumer. It will not subscribe on L1 updates
	 * directly and expects that some code outside will do it in appropriate
	 * moment. In this mode a special order tracker make reactor subscription
	 * for L1 updates each time when new order registered. When order removed
	 * then tracker closes subscription.
	 */
	USE_L1UPDATES_WHEN_ORDER_APPEARS
	
}
