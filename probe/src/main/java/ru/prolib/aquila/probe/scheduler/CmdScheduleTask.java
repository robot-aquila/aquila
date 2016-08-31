package ru.prolib.aquila.probe.scheduler;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CmdScheduleTask extends Cmd {
	private final SchedulerTask task;

	public CmdScheduleTask(SchedulerTask task) {
		super(CmdType.SCHEDULE_TASK);
		this.task = task;
	}
	
	public SchedulerTask getTask() {
		return task;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdScheduleTask.class ) {
			return false;
		}
		CmdScheduleTask o = (CmdScheduleTask) other;
		return new EqualsBuilder()
			.append(task, o.task)
			.isEquals();
	}
	
}
