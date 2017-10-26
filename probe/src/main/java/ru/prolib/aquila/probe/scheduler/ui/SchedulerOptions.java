package ru.prolib.aquila.probe.scheduler.ui;

import ru.prolib.aquila.core.data.ZTFrame;

public class SchedulerOptions {
	private int executionSpeed;
	private ZTFrame timeFrame;
	
	public void setExecutionSpeed(int speed) {
		this.executionSpeed = speed; 
	}
	
	public int getExecutionSpeed() {
		return executionSpeed;
	}
	
	public void setTimeFrame(ZTFrame timeFrame) {
		this.timeFrame = timeFrame;
	}
	
	public ZTFrame getTimeFrame() {
		return timeFrame;
	}
	
}
