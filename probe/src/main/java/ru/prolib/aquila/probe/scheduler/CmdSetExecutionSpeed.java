package ru.prolib.aquila.probe.scheduler;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CmdSetExecutionSpeed extends Cmd {
	private final int executionSpeed;

	public CmdSetExecutionSpeed(int executionSpeed) {
		super(CmdType.SET_EXECUTION_SPEED);
		if ( executionSpeed < 0 ) {
			throw new IllegalArgumentException("Execution speed must be positive or zero but: " + executionSpeed);
		}
		this.executionSpeed = executionSpeed;
	}
	
	public int getExecutionSpeed() {
		return executionSpeed;
	}
	
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdSetExecutionSpeed.class ) {
			return false;
		}
		CmdSetExecutionSpeed o = (CmdSetExecutionSpeed) other;
		return new EqualsBuilder()
			.append(executionSpeed, o.executionSpeed)
			.isEquals();
	}

}
