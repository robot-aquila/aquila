package ru.prolib.aquila.probe.scheduler;

public class CmdModeSwitchRun extends CmdModeSwitch {

	public CmdModeSwitchRun() {
		super(SchedulerMode.RUN);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdModeSwitchRun.class ) {
			return false;
		}
		return true;
	}

}
