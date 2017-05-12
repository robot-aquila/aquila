package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMExitAction;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SBullWaitSig extends BasicState implements SMExitAction {
	public static final String EOPN = Const.S_OPEN;
	public static final String ECLS = Const.S_CLOSE;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SBullWaitSig.class);
	}

	public SBullWaitSig(RobotData data) {
		super(data);
		registerExit(EOPN);
		registerExit(ECLS);
		setExitAction(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		triggers.add(newExitOnEvent(data.getSignal().onBullish(), EOPN));
		triggers.add(newExitOnEvent(data.getSignal().onBearish(), ECLS));
		return null;
	}

	@Override
	public void exit() {
		try {
			logger.debug(": " + data.getCandleSeries().get(-1));
		} catch ( Exception e ) { }
	}

}
