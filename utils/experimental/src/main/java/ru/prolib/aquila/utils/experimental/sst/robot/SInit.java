package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SInit extends BasicState {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SInit.class);
	}
	
	private final SMInput in;
	
	public SInit(RobotData data) {
		super(data);
		in = registerInput(this);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		triggers.add(new SMTriggerOnEvent(data.getTerminal().onSecurityAvailable(), in));
		triggers.add(new SMTriggerOnEvent(data.getTerminal().onPortfolioAvailable(), in));
		if ( objectsAvailable() ) {
			createIndicators();
			return getExit(EOK);
		}
		return null;
	}
	
	@Override
	public SMExit input(Object data) {
		if ( objectsAvailable() ) {
			createIndicators();
			return getExit(EOK);
		}
		return null;
	}

	private void createIndicators() {
		data.setCSFiller(new CSUtils().createFiller(data.getTerminal(), data.getSymbol(),
				TimeFrame.M1, data.getCandleSeries()));
		data.getCSFiller().start();
		data.getSignalProvider().start();
	}
	
	private boolean objectsAvailable() {
		boolean se = false, sa = false, ae = false, aa = false;
		se = data.getTerminal().isSecurityExists(data.getSymbol());
		ae = data.getTerminal().isPortfolioExists(data.getAccount());
		try {
	
			if ( se ) {
				sa = data.getTerminal().getSecurity(data.getSymbol()).isAvailable();
			}
			if ( ae ) {
				aa = data.getTerminal().getPortfolio(data.getAccount()).isAvailable();
			}
		} catch ( Exception e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
		logger.debug("state flags: se=" + se + " sa=" + sa + " ae=" + ae + " aa=" + aa);
		return se & sa && ae && aa;
	}

}
