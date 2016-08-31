package ru.prolib.aquila.probe.scheduler;

import java.time.Instant;

public class CmdShiftBackward extends Cmd {
	private final Instant time;
	
	public CmdShiftBackward(Instant time) {
		super(CmdType.SHIFT_BACKWARD);
		this.time = time;
	}
	
	public Instant getTime() {
		return time;
	}

}
