package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

public class CmdModeSwitchRunCutoff extends CmdModeSwitch {
	private final Instant cutoff;
	
	public CmdModeSwitchRunCutoff(Instant cutoff) {
		super(SchedulerMode.RUN_CUTOFF);
		this.cutoff = cutoff;
	}
	
	public Instant getCutoff() {
		return cutoff;
	}

}
