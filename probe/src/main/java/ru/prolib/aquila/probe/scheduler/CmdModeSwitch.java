package ru.prolib.aquila.probe.scheduler;

public abstract class CmdModeSwitch extends Cmd {
	private final SchedulerMode mode;
	
	public CmdModeSwitch(SchedulerMode mode) {
		super(CmdType.MODE_SWITCH);
		this.mode = mode;
	}
	
	public SchedulerMode getMode() {
		return mode;
	}

}
