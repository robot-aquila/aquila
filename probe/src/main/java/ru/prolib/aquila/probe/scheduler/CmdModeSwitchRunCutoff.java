package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CmdModeSwitchRunCutoff extends CmdModeSwitch {
	private final Instant cutoff;
	
	public CmdModeSwitchRunCutoff(Instant cutoff) {
		super(SchedulerMode.RUN_CUTOFF);
		this.cutoff = cutoff;
	}
	
	public Instant getCutoff() {
		return cutoff;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdModeSwitchRunCutoff.class ) {
			return false;
		}
		CmdModeSwitchRunCutoff o = (CmdModeSwitchRunCutoff) other;
		return new EqualsBuilder()
			.append(cutoff, o.cutoff)
			.isEquals();
	}

}
