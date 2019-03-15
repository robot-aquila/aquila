package ru.prolib.aquila.core.eque;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.utils.FlushControl;

public class EventQueueStats {
	private final AtomicLong buildTaskListTime, dispatchTime, deliveryTime,
		totalEventsSent, totalEventsDispatched;
	private final FlushControl flushControl;
	
	public EventQueueStats(FlushControl flushControl) {
		this.flushControl = flushControl;
		this.buildTaskListTime = new AtomicLong(0);
		this.dispatchTime = new AtomicLong(0);
		this.deliveryTime = new AtomicLong(0);
		this.totalEventsSent = new AtomicLong(0);
		this.totalEventsDispatched = new AtomicLong(0);
	}
	
	public void addEventSent() {
		totalEventsSent.incrementAndGet();
	}
	
	public void addEventDispatched() {
		flushControl.countDown();
		totalEventsDispatched.incrementAndGet();
	}
	
	public void addBuildingTaskListTime(long time) {
		buildTaskListTime.addAndGet(time);
	}

	public void addDispatchingTime(long time) {
		dispatchTime.addAndGet(time);
	}
	
	public void addDeliveryTime(long time) {
		deliveryTime.addAndGet(time);
	}
	
	public long getBuildingTaskListTime() {
		return buildTaskListTime.get();
	}
	
	public long getDispatchingTime() {
		return dispatchTime.get();
	}
	
	public long getDeliveryTime() {
		return deliveryTime.get();
	}
	
	public long getTotalEventsSent() {
		return totalEventsSent.get();
	}
	
	public long getTotalEventsDispatched() {
		return totalEventsDispatched.get();
	}
	
}
