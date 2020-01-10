package ru.prolib.aquila.core;

public interface EventQueueStats {

	long getPreparingTime();

	long getDispatchingTime();

	long getDeliveryTime();

	long getTotalEventsSent();

	long getTotalEventsDispatched();

}