package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMInput;
import ru.prolib.aquila.core.sm.SMInputAction;
import ru.prolib.aquila.core.sm.SMTriggerOnEvent;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SInit extends BasicState implements SMInputAction {
	public static final String EOK = Const.E_OK;
	
	private final SMInput in;
	
	public SInit(RobotData data) {
		super(data);
		registerExit(EOK);
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
		try {
			return data.getTerminal().isSecurityExists(data.getSymbol())
				&& data.getTerminal().isPortfolioExists(data.getAccount())
				&& data.getTerminal().getSecurity(data.getSymbol()).isAvailable()
				&& data.getTerminal().getPortfolio(data.getAccount()).isAvailable();
		} catch ( Exception e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		}
	}

}
