package ru.prolib.aquila.utils.experimental.sst.robot;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.tseries.CandleCloseTSeries;
import ru.prolib.aquila.core.data.tseries.CandleVolumeTSeries;
import ru.prolib.aquila.core.data.tseries.QEMATSeries;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesByLastTrade;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataProvider;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataProviderImpl;
import ru.prolib.aquila.utils.experimental.chart.data.ALOValidatorImpl;
import ru.prolib.aquila.utils.experimental.chart.data.OEDataProviderImpl;
import ru.prolib.aquila.utils.experimental.chart.data.OEEntrySet;
import ru.prolib.aquila.utils.experimental.chart.data.OEValidatorImpl;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;

public class RobotDataSliceTracker {
	private static final Logger logger;

	public static final String CANDLE_SERIES = "OHLC";
	public static final String CANDLE_CLOSE_SERIES = "CLOSE";
	public static final String QEMA7_CANDLE_CLOSE_SERIES = "QEMA(Close,7)";
	public static final String QEMA14_CANDLE_CLOSE_SERIES = "QEMA(Close,14)";
	public static final String CANDLE_VOLUME_SERIES = "Volume";
	public static final String ORDER_EXECUTION_SERIES = "ORDER_EXECUTIONS";
	public static final String ACTIVE_ORDERS = "ACTIVE_ORDERS";
	
	static {
		logger = LoggerFactory.getLogger(RobotDataSliceTracker.class);
	}
	
	private final SDP2DataSlice<SDP2Key> dataSlice;
	private final Account account;
	private final Terminal terminal;
	private boolean initialized = false, started = false;
	private OEDataProviderImpl executionsDataProvider;
	private CandleSeriesByLastTrade candleSeriesDataProvider;
	private ALODataProviderImpl orderDataProvider;

	/**
	 * Constructor.
	 * <p>
	 * @param dataSlice - data slice to manage all required data series
	 * @param account - account to track it's data
	 * @param terminal - terminal which provides scheduler capabilities, account and security data
	 */
	public RobotDataSliceTracker(SDP2DataSlice<SDP2Key> dataSlice, Account account, Terminal terminal) {
		this.dataSlice = dataSlice;
		this.account = account;
		this.terminal = terminal;
	}
	
	public SDP2DataSlice<SDP2Key> getDataSlice() {
		return dataSlice;
	}
	
	/**
	 * Initialize tracker and load historical data.
	 * <p>
	 * This method initializes all required series and loads historical data based on terminal's current time.
	 * <p>
	 * @param historicalDataStorage - storage of historical data
	 */
	public synchronized void initialize(MDStorage<TFSymbol, Candle> historicalDataStorage) {
		initialize(terminal.getCurrentTime(), historicalDataStorage);
	}
	
	/**
	 * Initialize tracker and load historical data.
	 * <p>
	 * @param currentTime - the end time of historical data to load
	 * @param historicalDataStorage - storage of historical data
	 */
	public synchronized void initialize(Instant currentTime, MDStorage<TFSymbol, Candle> historicalDataStorage) {
		if ( initialized ) {
			throw new IllegalStateException("Already initialized");
		}
		loadHistoricalData(createMainSeries(), currentTime, historicalDataStorage);
		createDerivedSeries();
		initialized = true;
	}
	
	/**
	 * Initialize tracker without loading of historical data.
	 */
	public synchronized void initialized() {
		if ( initialized ) {
			throw new IllegalStateException("Already initialized");
		}
		createMainSeries();
		createDerivedSeries();
		initialized = true;
		
	}
	
	private EditableTSeries<Candle> createMainSeries() {
		return dataSlice.createSeries(CANDLE_SERIES, true);
	}
	
	private void loadHistoricalData(EditableTSeries<Candle> candleSeries,
									Instant currentTime,
									MDStorage<TFSymbol, Candle> historicalDataStorage)
	{
		TFSymbol tfs = new TFSymbol(dataSlice.getSymbol(), dataSlice.getTimeFrame());
		try ( CloseableIterator<Candle> it = historicalDataStorage.createReader(tfs, 15, currentTime) ) {
			while ( it.next() ) {
				candleSeries.set(it.item().getStartTime(), it.item());
			}
		} catch ( Exception e ) {
			logger.error("Error loading history: ", e);
		}
	}
	
	private void createDerivedSeries() {
		TSeries<Candle> x = dataSlice.getSeries(CANDLE_SERIES);
		EditableTSeries<Candle> candleSeries = (EditableTSeries<Candle>) x;
		TSeries<CDecimal> closeSeries = new CandleCloseTSeries(CANDLE_CLOSE_SERIES, candleSeries);
		TSeries<CDecimal> qema7Series = new QEMATSeries(QEMA7_CANDLE_CLOSE_SERIES, closeSeries, 7);
		TSeries<CDecimal> qema14Series = new QEMATSeries(QEMA14_CANDLE_CLOSE_SERIES, closeSeries, 14);
		TSeries<CDecimal> volumeSeries = new CandleVolumeTSeries(CANDLE_VOLUME_SERIES, candleSeries);
		dataSlice.registerRawSeries(closeSeries);
		dataSlice.registerRawSeries(qema7Series);
		dataSlice.registerRawSeries(qema14Series);
		dataSlice.registerRawSeries(volumeSeries);
		EditableTSeries<OEEntrySet> executions = dataSlice.createSeries(ORDER_EXECUTION_SERIES, false);

		// Initialize order executions data provider for selected symbol and account
		OEValidatorImpl oeValidator = new OEValidatorImpl();
		oeValidator.addFilterBySymbol(dataSlice.getSymbol());
		oeValidator.addFilterByAccount(account);
		executionsDataProvider = new OEDataProviderImpl(executions, oeValidator);
		
		// Initialize active limit orders data provider for selected symbol and account
		ALOValidatorImpl aloValidator = new ALOValidatorImpl();
		aloValidator.addFilterBySymbol(dataSlice.getSymbol());
		aloValidator.addFilterByAccount(account);
		orderDataProvider = new ALODataProviderImpl(aloValidator);
		
		// Initialize candle series filler
		candleSeriesDataProvider = new CandleSeriesByLastTrade(candleSeries, terminal, dataSlice.getSymbol());
	}
	
	public synchronized void startDataTracking() {
		if ( ! initialized ) {
			throw new IllegalStateException("Object not initialized");
		}
		if ( started ) {
			throw new IllegalStateException("Data tracking already started");
		}
		executionsDataProvider.addTerminal(terminal);
		orderDataProvider.addTerminal(terminal);
		candleSeriesDataProvider.start();
		started = true;
	}
	
	public synchronized void stopDataTracking() {
		if ( ! started ) {
			return;
		}
		executionsDataProvider.removeTerminal(terminal);
		orderDataProvider.removeTerminal(terminal);
		candleSeriesDataProvider.stop();
		started = false;
	}
	
	public synchronized ALODataProvider getALODataProvider() {
		if ( ! initialized ) {
			throw new IllegalStateException("Object not initialized");
		}
		return orderDataProvider;
	}

}
