package ru.prolib.aquila.utils.experimental.sst.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.sm.SMExit;
import ru.prolib.aquila.core.sm.SMTriggerRegistry;
import ru.prolib.aquila.utils.experimental.sst.msig.sp.CMASignalProviderTS;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;

public class SHInitMarketSignal extends BasicStateHandler {
	public static final String EOK = Const.E_OK;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SHInitMarketSignal.class);
	}
	
	private final DataServiceLocator serviceLocator;

	public SHInitMarketSignal(RobotState data, DataServiceLocator serviceLocator) {
		super(data);
		this.serviceLocator = serviceLocator;
		registerExit(EOK);
	}
	
	@Override
	public SMExit enter(SMTriggerRegistry triggers) {
		super.enter(triggers);
		
		// Initialize data slice
		RobotConfig config = data.getConfig();
		SDP2Key sliceKey = new SDP2Key(config.getTFrame(), config.getSymbol());
		SDP2DataSlice<SDP2Key> dataSlice = serviceLocator.getSliceDataRegistry().createSlice(sliceKey);
		RobotDataSliceTracker sliceTracker = new RobotDataSliceTracker(dataSlice, config.getAccount(), data.getTerminal());
		sliceTracker.initialize(serviceLocator.getHistoricalDataStorage());
		sliceTracker.startDataTracking();
		data.setDataSliceTracker(sliceTracker);
		
		// Initialize market signal
		String signalID = config.getSymbol() + "_" + config.getTFrame() + "_" + "_CMA(7,14)";
		serviceLocator.getMarketSignalRegistry().register(new CMASignalProviderTS(
				dataSlice.getObservableSeries(RobotDataSliceTracker.CANDLE_SERIES),
				dataSlice.getSeries(RobotDataSliceTracker.QEMA7_CANDLE_CLOSE_SERIES),
				dataSlice.getSeries(RobotDataSliceTracker.QEMA14_CANDLE_CLOSE_SERIES),
				data.getEventQueue(), signalID));
		data.setMarketSignal(serviceLocator.getMarketSignalRegistry().getSignal(signalID));
		logger.debug("Market signal initialized: {}", signalID);
		
		data.getStateListener().robotStarted();
		
		return getExit(EOK);
	}

}
