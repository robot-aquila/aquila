package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.sm.SMStateMachine;

public class Robot {
	private final SMStateMachine automat;
	private final RobotState data;
	
	public Robot(SMStateMachine automat, RobotState data) {
		this.automat = automat;
		this.data = data;
	}
	
	public SMStateMachine getAutomat() {
		return automat;
	}
	
	public RobotState getData() {
		return data;
	}

}
