package ru.prolib.aquila.utils.experimental.sst.robot;

import java.io.File;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.core.utils.PriceScaleDBLazy;
import ru.prolib.aquila.core.utils.PriceScaleDBTB;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceImpl;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceSATImpl;
import ru.prolib.aquila.probe.datasim.SymbolUpdateSourceImpl;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.qforts.impl.QFortsEnv;
import ru.prolib.aquila.utils.experimental.sst.DataProviderImpl;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalRegistry;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalRegistryImpl;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataProvider;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataProviderImpl;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSliceFactoryImpl;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.aquila.web.utils.finam.datasim.FinamL1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;
import ru.prolib.aquila.web.utils.moex.MoexSymbolUpdateReaderFactory;

public class DataServiceLocator {
	private final MarketSignalRegistry msigRegistry = new MarketSignalRegistryImpl();
	private final PriceScaleDBLazy priceScaleDB = new PriceScaleDBLazy();
	private File dataRoot;
	private Scheduler scheduler;
	private QFBuilder qfBuilder = new QFBuilder();
	private EditableTerminal terminal;
	private QFortsEnv qfEnv;
	private SDP2DataProvider<SDP2Key> sliceDataRegistry;
	private MDStorage<TFSymbol, Candle> historicalDataStorage;
	private MoexContractFileStorage contractDataStorage;
	
	public MarketSignalRegistry getMarketSignalRegistry() {
		return msigRegistry;
	}
	
	public PriceScaleDB getPriceScaleDB() {
		return priceScaleDB;
	}
	
	public synchronized void setDataRootDirectory(File dir) {
		if ( dataRoot != null ) {
			throw new IllegalStateException("Data root directory already defined");
		}
		this.dataRoot = dir;
		historicalDataStorage = createHistoricalDataStorage();
		contractDataStorage = createContractDataStorage();
	}
	
	public synchronized File getDataRootDirectory() {
		if ( dataRoot == null ) {
			throw new IllegalStateException("Data root directory is not defined");
		}
		return dataRoot;
	}
	
	public synchronized MDStorage<TFSymbol, Candle> getHistoricalDataStorage() {
		if ( historicalDataStorage == null ) {
			throw new IllegalStateException("Historical data storage is not defined");
		}
		return historicalDataStorage;
	}
	
	public synchronized MoexContractFileStorage getContractDataStorage() {
		if ( contractDataStorage == null ) {
			throw new IllegalStateException("Contract data storage is not initialized");
		}
		return contractDataStorage;
	}
	
	public synchronized void setScheduler(Scheduler scheduler) {
		if ( this.scheduler != null ) {
			throw new IllegalStateException("Scheduler already defined");
		}
		this.scheduler = scheduler;
		setTerminal(createTerminal());
	}
	
	public synchronized Scheduler getScheduler() {
		if ( scheduler == null ) {
			throw new IllegalStateException("Scheduler is not defined");
		}
		return scheduler;
	}
	
	public synchronized void setTerminal(EditableTerminal terminal) {
		if ( this.terminal != null ) {
			throw new IllegalStateException("Terminal already defined");
		}
		this.terminal = terminal;
		priceScaleDB.setParentDB(new PriceScaleDBTB(terminal));
		qfEnv = createQFortsEnvironment();
		sliceDataRegistry = createSliceDataRegistry();
	}
	
	public synchronized EditableTerminal getTerminal() {
		if ( terminal == null ) {
			throw new IllegalStateException("Terminal is not defined");
		}
		return terminal;
	}
	
	public synchronized QFortsEnv getQFortsEnv() {
		if ( qfEnv == null ) {
			throw new IllegalStateException("QForts environment is not defined");
		}
		return qfEnv;
	}
	
	public synchronized SDP2DataProvider<SDP2Key> getSliceDataRegistry() {
		if ( sliceDataRegistry == null ) {
			throw new IllegalStateException("Slice data registry is not defined");
		}
		return sliceDataRegistry;
	}
	
	private DataProvider createDataProvider() {
		File root = getDataRootDirectory();
		return new DataProviderImpl(
				new SymbolUpdateSourceImpl(scheduler, new MoexSymbolUpdateReaderFactory(root)),
				new L1UpdateSourceSATImpl(new L1UpdateSourceImpl(getScheduler(), new FinamL1UpdateReaderFactory(root, getPriceScaleDB()))),
				qfBuilder.buildDataProvider());
	}
	
	private EditableTerminal createTerminal() {
		return new BasicTerminalBuilder()
			.withTerminalID("SECURITY_SIMULATION")
			.withScheduler(getScheduler())
			.withDataProvider(createDataProvider())
			.buildTerminal();
	}
	
	private QFortsEnv createQFortsEnvironment() {
		 return qfBuilder.buildEnvironment(getTerminal());
	}
	
	private SDP2DataProvider<SDP2Key> createSliceDataRegistry() {
		return new SDP2DataProviderImpl<SDP2Key>(new SDP2DataSliceFactoryImpl<>(getTerminal().getEventQueue()));
	}
	
	private MDStorage<TFSymbol, Candle> createHistoricalDataStorage() {
		try {
			return new FinamData().createCachingOHLCV(getDataRootDirectory(),
					new File(System.getProperty("java.io.tmpdir") + File.separator + "aquila-ohlcv-cache"),
					getPriceScaleDB());
		} catch ( DataStorageException e ) {
			throw new IllegalStateException("Historical data storage initialization failed: ", e);
		}
	}
	
	private MoexContractFileStorage createContractDataStorage() {
		return new MoexContractFileStorage(getDataRootDirectory());
	}

}
