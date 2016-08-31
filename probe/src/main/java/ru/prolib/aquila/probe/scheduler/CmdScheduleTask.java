package ru.prolib.aquila.probe.scheduler;

public class CmdScheduleTask extends Cmd {
	private final SchedulerTask task;

	public CmdScheduleTask(SchedulerTask task) {
		super(CmdType.SCHEDULE_TASK);
		this.task = task;
	}
	
	public SchedulerTask getTask() {
		return task;
	}
	
}
