package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;

public class SHDropMarketSignal extends BasicState {
	public static final String EOK = Const.E_OK;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SHDropMarketSignal.class);
	}
	
	private final DataServiceLocator serviceLocator;

	public SHDropMarketSignal(RobotData data, DataServiceLocator serviceLocator) {
		super(data);
		this.serviceLocator = serviceLocator;
		registerExit(EOK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		
		// Stop tracking signal data
		String signalID = data.getMarketSignal().getID();
		serviceLocator.getMarketSignalRegistry().remove(signalID);
		logger.debug("Market signal removed: {}", signalID);
		
		// Stop filling the data slice
		data.getDataSliceTracker().stopDataTracking();

		data.getStateListener().robotStopped();
		
		return getExit(EOK);
	}

}
