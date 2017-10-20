package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CmdShiftForward extends Cmd {
	private final Instant time;
	
	public CmdShiftForward(Instant time) {
		super(CmdType.SHIFT_FORWARD);
		this.time = time;
	}
	
	public Instant getTime() {
		return time;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdShiftForward.class ) {
			return false;
		}
		CmdShiftForward o = (CmdShiftForward) other;
		return new EqualsBuilder()
			.append(time, o.time)
			.isEquals();
	}

}
