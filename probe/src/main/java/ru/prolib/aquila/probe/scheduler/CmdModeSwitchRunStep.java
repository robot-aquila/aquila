package ru.prolib.aquila.probe.scheduler;

public class CmdModeSwitchRunStep extends CmdModeSwitch {

	public CmdModeSwitchRunStep() {
		super(SchedulerMode.RUN_STEP);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdModeSwitchRunStep.class ) {
			return false;
		}
		return true;
	}

}
