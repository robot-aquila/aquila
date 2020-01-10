package ru.prolib.aquila.core.eqs;

public abstract class Cmd {
	protected final CmdType type;
	
	public Cmd(CmdType type) {
		this.type = type;
	}
	
	public CmdType getType() {
		return type;
	}

}
