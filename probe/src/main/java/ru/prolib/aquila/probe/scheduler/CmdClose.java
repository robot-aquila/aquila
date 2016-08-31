package ru.prolib.aquila.probe.scheduler;

public class CmdClose extends Cmd {

	public CmdClose() {
		super(CmdType.CLOSE);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdClose.class ) {
			return false;
		}
		return true;
	}
	
}
