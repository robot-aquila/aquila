package ru.prolib.aquila.probe.scheduler;

public enum CmdType {
	
	/**
	 * Command to close scheduler.
	 */
	CLOSE,

	/**
	 * Command to switch scheduler mode.
	 */
	MODE_SWITCH,
	
	/**
	 * Shift the current time forward.
	 */
	SHIFT_FORWARD,
	
	/**
	 * Shift the current time backward.
	 */
	SHIFT_BACKWARD,
	
	/**
	 * Schedule a new task.
	 */
	SCHEDULE_TASK,
	
	/**
	 * Set execution speed.
	 */
	SET_EXECUTION_SPEED,

}
