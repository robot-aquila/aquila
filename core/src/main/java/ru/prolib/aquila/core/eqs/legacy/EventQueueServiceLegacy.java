package ru.prolib.aquila.core.eqs.legacy;

import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.eque.EventQueueService;

@Deprecated
public class EventQueueServiceLegacy implements EventQueueService {
	private final FlushControl flushControl;
	private final EventQueueStatsLegacy queueStats;
	
	public EventQueueServiceLegacy(FlushControl flush_control, EventQueueStatsLegacy queue_stats) {
		this.flushControl = flush_control;
		this.queueStats = queue_stats;
	}
	
	public EventQueueServiceLegacy() {
		this(new FlushControl(), new EventQueueStatsLegacy());
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
		flushControl.countDown();
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
	public EventQueueStatsLegacy getStats() {
		return queueStats;
	}

	@Override
	public void shutdown() {
		queueStats.dumpSecondaryStats();
	}

}
