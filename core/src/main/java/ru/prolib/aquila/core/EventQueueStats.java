package ru.prolib.aquila.core;

import java.util.concurrent.atomic.AtomicLong;

public class EventQueueStats {
	private final AtomicLong buildTaskListTime, dispatchTime, deliveryTime;
	
	public EventQueueStats() {
		this.buildTaskListTime = new AtomicLong(0);
		this.dispatchTime = new AtomicLong(0);
		this.deliveryTime = new AtomicLong(0);
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
	
}
