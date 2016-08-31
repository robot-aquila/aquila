package ru.prolib.aquila.probe.scheduler;


public class CmdModeSwitchWait extends CmdModeSwitch {

	public CmdModeSwitchWait() {
		super(SchedulerMode.WAIT);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdModeSwitchWait.class ) {
			return false;
		}
		return true;
	}

}
