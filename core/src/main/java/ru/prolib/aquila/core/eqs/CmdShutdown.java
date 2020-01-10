package ru.prolib.aquila.core.eqs;

public class CmdShutdown extends Cmd {

	public CmdShutdown() {
		super(CmdType.SHUTDOWN);
	}
	
	@Override
	public int hashCode() {
		return 66641497;
	}

	@Override
	public boolean equals(Object other) {
		if  ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdShutdown.class ) {
			return false;
		}
		return true;
	}
	
}
