package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CmdShiftBackward extends Cmd {
	private final Instant time;
	
	public CmdShiftBackward(Instant time) {
		super(CmdType.SHIFT_BACKWARD);
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
		if ( other == null || other.getClass() != CmdShiftBackward.class ) {
			return false;
		}
		CmdShiftBackward o = (CmdShiftBackward) other;
		return new EqualsBuilder()
			.append(time, o.time)
			.isEquals();
	}

}
