package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.sm.SMEnterAction;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInputStub;
import ru.prolib.aquila.core.sm.SMState;
import ru.prolib.aquila.core.sm.SMTrigger;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

abstract public class BasicState extends SMState implements SMEnterAction {
	public static final String EER = Const.E_ERROR;
	public static final String EBR = Const.E_BREAK;
	protected final RobotData data;
	
	public BasicState(RobotData data) {
		this.data = data;
		setEnterAction(this);
		registerExit(EER);
		registerExit(EBR);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		triggers.add(newExitOnEvent(data.getBreakSignal().onBreak(), EBR));
		return null;
	}
	
	protected SMTrigger newExitOnEvent(EventType type, String exitID) {
		return new SMTriggerOnEvent(type, registerInput(new SMInputStub(getExit(exitID))));
	}

}
