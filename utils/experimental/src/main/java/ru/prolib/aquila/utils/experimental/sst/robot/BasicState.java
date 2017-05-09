package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.sm.SMEnterAction;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMInputStub;
import ru.prolib.aquila.core.sm.SMState;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

abstract public class BasicState extends SMState implements SMEnterAction, SMInputAction  {
	public static final String EOK = Const.E_OK;
	public static final String EER = Const.E_ERROR;
	public static final String EBR = Const.E_BREAK;
	protected final RobotData data;
	
	public BasicState(RobotData data) {
		this.data = data;
		setEnterAction(this);
		registerInput(this);
		registerExit(EOK);
		registerExit(EER);
		registerExit(EBR);
	}

	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		triggers.add(new SMTriggerOnEvent(data.getSignal().onBreak(),
				registerInput(new SMInputStub(getExit(EBR)))));
		return null;
	}

}
