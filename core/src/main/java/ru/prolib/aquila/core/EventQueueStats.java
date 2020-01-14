package ru.prolib.aquila.core;

public interface EventQueueStats {

	long getPreparingTime();

	long getDispatchingTime();

	long getDeliveryTime();
	
	long getTotalEventsEnqueued();

	long getTotalEventsSent();

	long getTotalEventsDispatched();

}