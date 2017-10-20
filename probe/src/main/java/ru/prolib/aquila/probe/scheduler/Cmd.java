package ru.prolib.aquila.probe.scheduler;

public abstract class Cmd {
	private final CmdType type;
	
	public Cmd(CmdType type) {
		this.type = type;
	}
	
	public CmdType getType() {
		return type;
	}

}
