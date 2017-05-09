package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SWaitSig extends BasicState {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWaitSig.class);
	}
	
	private final SMInput in;

	public SWaitSig(RobotData data) {
		super(data);
		in = registerInput(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		triggers.add(new SMTriggerOnEvent(data.getSignal().onBullish(), in));
		triggers.add(new SMTriggerOnEvent(data.getSignal().onBearish(), in));
		return null;
	}

	@Override
	public SMExit input(Object data) {
		logger.debug("Incoming event: {}", data);
		return null;
	}

}
