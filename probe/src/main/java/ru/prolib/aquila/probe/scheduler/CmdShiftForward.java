package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

public class CmdShiftForward extends Cmd {
	private final Instant time;
	
	public CmdShiftForward(Instant time) {
		super(CmdType.SHIFT_FORWARD);
		this.time = time;
	}
	
	public Instant getTime() {
		return time;
	}

}
