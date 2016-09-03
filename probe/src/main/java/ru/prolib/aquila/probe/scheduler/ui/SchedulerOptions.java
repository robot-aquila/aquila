package ru.prolib.aquila.probe.scheduler.ui;

import ru.prolib.aquila.core.data.TimeFrame;

public class SchedulerOptions {
	private int executionSpeed;
	private TimeFrame timeFrame = TimeFrame.M1;
	
	public void setExecutionSpeed(int speed) {
		this.executionSpeed = speed; 
	}
	
	public int getExecutionSpeed() {
		return executionSpeed;
	}
	
	public void setTimeFrame(TimeFrame timeFrame) {
		this.timeFrame = timeFrame;
	}
	
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}
	
}
