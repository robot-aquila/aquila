package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMInputStub;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SBullWaitSig extends BasicState implements SMExitAction, SMInputAction {
	public static final String EOPN = Const.S_OPEN;
	public static final String ECLS = Const.S_CLOSE;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SBullWaitSig.class);
	}
	
	private final SMInput in;

	public SBullWaitSig(RobotData data) {
		super(data);
		registerExit(EOPN);
		registerExit(ECLS);
		setExitAction(this);
		in = registerInput(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		//triggers.add(newExitOnEvent(data.getSignal().onBullish(), EOPN));
		//triggers.add(newExitOnEvent(data.getSignal().onBearish(), ECLS));
		triggers.add(new SMTriggerOnEvent(data.getSignal().onBullish(), in));
		triggers.add(new SMTriggerOnEvent(data.getSignal().onBearish(), in));
		return null;
	}

	@Override
	public void exit() {

	}

	@Override
	public SMExit input(Object dummy) {
		logger.debug("Input: {}", dummy);
		Event e = (Event) dummy;
		if ( e.isType(data.getSignal().onBullish()) ) {
			return getExit(EOPN);
		} else
		if ( e.isType(data.getSignal().onBearish()) ) {
			return getExit(ECLS);
		}
		return null;
	}

}
