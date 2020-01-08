package ru.prolib.aquila.core.eque;

import ru.prolib.aquila.core.utils.FlushControl;
import ru.prolib.aquila.core.utils.FlushIndicator;

public class EventQueueServiceImpl implements EventQueueService {
	private final FlushControl flushControl;
	private final EventQueueStats queueStats;
	
	public EventQueueServiceImpl(FlushControl flush_control, EventQueueStats queue_stats) {
		this.flushControl = flush_control;
		this.queueStats = queue_stats;
	}

	@Override
	public FlushIndicator createIndicator() {
		return flushControl.createIndicator();
	}

	@Override
	public void eventEnqueued() {
		flushControl.countUp();
	}

	@Override
	public void eventSent() {
		queueStats.addEventSent();
	}

	@Override
	public void eventDispatched() {
		queueStats.addEventDispatched();
	}

	@Override
	public void addPreparingTime(long nanos) {
		queueStats.addBuildingTaskListTime(nanos);
	}

	@Override
	public void addDispatchingTime(long nanos) {
		queueStats.addDispatchingTime(nanos);
	}

	@Override
	public void addDeliveryTime(long nanos) {
		queueStats.addDeliveryTime(nanos);
	}

	@Override
	public EventQueueStats getStats() {
		return queueStats;
	}

}
