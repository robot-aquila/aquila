package ru.prolib.aquila.utils.experimental.sst;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.timeframe.ZTFHours;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;
import ru.prolib.aquila.core.data.tseries.CandleCloseTSeries;
import ru.prolib.aquila.core.data.tseries.CandleVolumeTSeries;
import ru.prolib.aquila.core.data.tseries.QEMATSeries;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesByLastTrade;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceImpl;
import ru.prolib.aquila.probe.datasim.L1UpdateSourceSATImpl;
import ru.prolib.aquila.probe.datasim.SymbolUpdateSourceImpl;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerControlToolbar;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerTaskFilter;
import ru.prolib.aquila.probe.scheduler.ui.SymbolUpdateTaskFilter;
import ru.prolib.aquila.probe.scheduler.utils.EventQueueSynchronizer;
import ru.prolib.aquila.qforts.impl.QFBuilder;
import ru.prolib.aquila.qforts.impl.QFTransactionException;
import ru.prolib.aquila.qforts.impl.QFortsEnv;
import ru.prolib.aquila.qforts.ui.QFPortfolioListTableModel;
import ru.prolib.aquila.qforts.ui.QFPositionListTableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OrderListTableModel;
import ru.prolib.aquila.ui.form.PortfolioListTableModel;
import ru.prolib.aquila.ui.form.PositionListTableModel;
import ru.prolib.aquila.ui.form.SecurityListTableModel;
import ru.prolib.aquila.utils.experimental.CmdLine;
import ru.prolib.aquila.utils.experimental.Experiment;
import ru.prolib.aquila.utils.experimental.chart.BarChart;
import ru.prolib.aquila.utils.experimental.chart.BarChartOrientation;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelHandler;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.CandleBarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.CurrentValueLayer;
import ru.prolib.aquila.utils.experimental.sst.msig.sp.CMASignalProviderTS;
import ru.prolib.aquila.utils.experimental.sst.sdp2.*;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignal;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalRegistry;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalRegistryImpl;
import ru.prolib.aquila.utils.experimental.sst.robot.Robot;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotBuilder;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotConfig;
import ru.prolib.aquila.utils.experimental.chart.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.chart.formatters.NumberLabelFormatter;
import ru.prolib.aquila.web.utils.finam.data.FinamData;
import ru.prolib.aquila.web.utils.finam.datasim.FinamL1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;
import ru.prolib.aquila.web.utils.moex.MoexSymbolUpdateReaderFactory;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.BAR_COLOR;

public class SecuritySimulationTest implements Experiment {
	private static final Logger logger;

	private static final String CANDLE_SERIES = "OHLC";
	private static final String CANDLE_CLOSE_SERIES = "CLOSE";
	private static final String QEMA7_CANDLE_CLOSE_SERIES = "QEMA(Close,7)";
	private static final String QEMA14_CANDLE_CLOSE_SERIES = "QEMA(Close,14)";
	private static final String CANDLE_VOLUME_SERIES = "Volume";
	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	private BarChartPanelHandler chartPanelHandler;
	
	static {
		logger = LoggerFactory.getLogger(SecuritySimulationTest.class);
	}
	
	private JTabbedPane tabPanel;
	private EditableTerminal terminal;
	private MarketSignal signal;
	private SDP2DataProvider<SDP2Key> sdp2DataProvider;
	private final MarketSignalRegistry msigRegistry = new MarketSignalRegistryImpl();

	@Override
	public void close() throws IOException {
		if ( signal != null ) {
			signal.fireBreak();
			signal = null;
		}
		if ( sdp2DataProvider != null ) {
//			sdp2DataProvider.stop();
			sdp2DataProvider = null;
		}
		terminal.stop();
	}

	@Override
	public int run(Scheduler scheduler, CommandLine cmd, CountDownLatch exitSignal) {
		final IMessages messages = new Messages();
		final File root = new File(cmd.getOptionValue(CmdLine.LOPT_ROOT));
		
		// Initialize terminal
		QFBuilder qfBuilder = new QFBuilder();
		terminal = new BasicTerminalBuilder()
			.withTerminalID("SECURITY_SIMULATION")
			.withScheduler(scheduler)
			.withDataProvider(newDataProvider(scheduler, root, qfBuilder.buildDataProvider()))
			.buildTerminal();
		if ( scheduler.getClass() == SchedulerImpl.class ) {
			SchedulerImpl s = (SchedulerImpl) scheduler;
			s.addSynchronizer(new EventQueueSynchronizer(terminal.getEventQueue()));
		}
		QFortsEnv qfEnv = qfBuilder.buildEnvironment(terminal);
		try {
			qfEnv.createPortfolio(new Account("TEST-ACCOUNT"), CDecimalBD.ofRUB2("300000"));
		} catch ( QFTransactionException e ) {
			logger.error("Error creating test portfolio: ", e);
			return 1;
		}
		Set<Symbol> symbols = null;
		try {
			symbols = new MoexContractFileStorage(root).getSymbols();
		} catch ( DataStorageException e ) {
			logger.error("Error reading symbol list: ", e);
			return 1;
		}
		//for ( Symbol symbol : symbols ) {
		//	terminal.subscribe(symbol);
		//}
		final Set<Symbol> symbols_dup = symbols;
		terminal.onTerminalReady().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				Instant t = terminal.getCurrentTime();
				logger.debug("Terminal ready at {}", t);
				for ( Symbol s : symbols_dup ) {
					terminal.subscribe(s);
				}
			}
		});
		terminal.start();
		
		sdp2DataProvider = new SDP2DataProviderImpl<SDP2Key>(new SDP2DataSliceFactoryImpl<>(terminal.getEventQueue()));

		final List<SDP2DataSlice<SDP2Key>> slices = new ArrayList<>();

		Symbol rSymbol = new Symbol(cmd.getOptionValue(CmdLine.LOPT_SYMBOL, "Si-9.16"));
		ZTFrame tf = CmdLine.getTimeFrame(cmd,ZONE_ID);
		logger.debug("Selected strategy symbol: {}", rSymbol);
		logger.debug("Selected timeframe: {}", tf);
		final SDP2DataSlice<SDP2Key> slice = sdp2DataProvider.getSlice(new SDP2Key(tf, rSymbol));
		String signalID = rSymbol + "_CMA(7, 14)";
		int i = createSeriesBySlice(slice, root, scheduler);
		if(i!=0){
			return i;
		}
		slices.add(slice);

		// Additional timeframes
		ZTFrame[] timeFrames = {
				new ZTFMinutes(5, ZONE_ID),
				new ZTFMinutes(15, ZONE_ID),
				new ZTFHours(1, ZONE_ID),
		};
		for(ZTFrame timeFrame: timeFrames){
			final SDP2DataSlice<SDP2Key> tempSlice = sdp2DataProvider.getSlice(new SDP2Key(timeFrame, rSymbol));
			i = createSeriesBySlice(tempSlice, root, scheduler);
			if(i!=0){
				return i;
			}
			slices.add(tempSlice);
		}

		terminal.onSecurityAvailable().listenOnce(new EventListener() {
			@Override
			public void onEvent(Event event) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JPanel chartRoot = new JPanel(new GridLayout(2, 2));
						for(SDP2DataSlice<SDP2Key> s: slices){
							BarChartPanelImpl<Instant> chartPanel = createChartPanel(s);
							chartRoot.add(chartPanel.getRootPanel());
							chartPanel.setVisibleArea(0, chartPanel.getNumberOfVisibleCategories());
						}
						tabPanel.addTab("Strategy", chartRoot);
					}
				});
			}
		});

		msigRegistry.register(new CMASignalProviderTS(
				slice.getObservableSeries(CANDLE_SERIES),
				slice.getSeries(QEMA7_CANDLE_CLOSE_SERIES),
				slice.getSeries(QEMA14_CANDLE_CLOSE_SERIES),
				terminal.getEventQueue(), signalID));


		RobotConfig rConfig = new RobotConfig(rSymbol, new Account("TEST-ACCOUNT"), 0.5d, signalID);
		Robot robot = new RobotBuilder(terminal, msigRegistry).buildBullDummy(rConfig);
		robot.getAutomat().setDebug(true);
		signal = robot.getData().getSignal();
		try {
			robot.getAutomat().start();
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
			return 2;
		}

		// Initialize the main frame
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				exitSignal.countDown();
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800,  600);
		frame.setTitle("Security simulation test");

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        mainPanel.add(topPanel, BorderLayout.PAGE_START);
		if ( scheduler instanceof ru.prolib.aquila.probe.SchedulerImpl ) {
			List<SchedulerTaskFilter> filters = new ArrayList<>();
			filters.add(new SymbolUpdateTaskFilter(messages));
			topPanel.add(new SchedulerControlToolbar(messages, (SchedulerImpl) scheduler, ZONE_ID, filters));
		}

		tabPanel = new JTabbedPane();
        mainPanel.add(tabPanel, BorderLayout.CENTER);

        SecurityListTableModel securityTableModel = new SecurityListTableModel(messages);
        securityTableModel.add(terminal);
        JTable table = new JTable(securityTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<SecurityListTableModel>(securityTableModel));
        tabPanel.add("Securities", new JScrollPane(table));
        new TableModelController(securityTableModel, frame);

        OrderListTableModel orderTableModel = new OrderListTableModel(messages);
        orderTableModel.add(terminal);
        table = new JTable(orderTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<>(orderTableModel));
        tabPanel.add("Orders", new JScrollPane(table));
        new TableModelController(orderTableModel, frame);

        QFPortfolioListTableModel portfolioTableModel = new QFPortfolioListTableModel(messages);
        portfolioTableModel.add(terminal);
        table = new JTable(portfolioTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<PortfolioListTableModel>(portfolioTableModel));
        tabPanel.add("Accounts", new JScrollPane(table));
        new TableModelController(portfolioTableModel, frame);

        QFPositionListTableModel positionTableModel = new QFPositionListTableModel(messages);
        positionTableModel.add(terminal);
        table = new JTable(positionTableModel);
        table.setShowGrid(true);
        table.setRowSorter(new TableRowSorter<PositionListTableModel>(positionTableModel));
        tabPanel.add("Positions", new JScrollPane(table));
        new TableModelController(positionTableModel, frame);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

		return 0;
	}

	@Override
	public int getExitCode() {
		return 0;
	}

	private DataProvider newDataProvider(Scheduler scheduler, File root, DataProvider parent) {
		return new DataProviderImpl(
			new SymbolUpdateSourceImpl(scheduler, new MoexSymbolUpdateReaderFactory(root)),
			new L1UpdateSourceSATImpl(new L1UpdateSourceImpl(scheduler, new FinamL1UpdateReaderFactory(root))),
			parent);
	}

	private BarChartPanelImpl<Instant> createChartPanel(SDP2DataSlice<SDP2Key> slice){
		BarChartPanelImpl<Instant> chartPanel = new BarChartPanelImpl<>(BarChartOrientation.LEFT_TO_RIGHT);
		Integer precision = null;
		try {
			precision = terminal.getSecurity(slice.getSymbol()).getScale();
		} catch ( SecurityException e ) {
			logger.error("Unexpected exception: ", e);
			precision = 0;
		}
		BarChart<Instant> chart = chartPanel.addChart("CANDLES")
				.setHeight(600)
				.setValuesLabelFormatter(new NumberLabelFormatter().withPrecision(precision))
				.addStaticOverlay(slice.getSymbol()+", "+slice.getTimeFrame().toTFrame().toString(), 0);
		chart.getTopAxis().setLabelFormatter(new InstantLabelFormatter(ZONE_ID));
		chart.getBottomAxis().setLabelFormatter(new InstantLabelFormatter(ZONE_ID));
		chart.addLayer(new CandleBarChartLayer<>(slice.getSeries(CANDLE_SERIES), ZONE_ID));
		chart.addSmoothLine(slice.getSeries(QEMA7_CANDLE_CLOSE_SERIES)).setColor(Color.BLUE);
		chart.addSmoothLine(slice.getSeries(QEMA14_CANDLE_CLOSE_SERIES)).setColor(Color.MAGENTA);
		chart.addLayer(new CurrentValueLayer<>(slice.getSeries(CANDLE_CLOSE_SERIES)));

		chart = chartPanel.addChart("VOLUMES")
				.setHeight(200)
				.setValuesLabelFormatter(new NumberLabelFormatter().withPrecision(0))
				.addStaticOverlay("Volume", 0);
		chart.getTopAxis().setLabelFormatter(new InstantLabelFormatter(ZONE_ID));
		chart.getBottomAxis().setLabelFormatter(new InstantLabelFormatter(ZONE_ID));
		chart.addHistogram(slice.getSeries(CANDLE_VOLUME_SERIES)).setColor(BAR_COLOR);

		chartPanel.setCategories(slice.getIntervalStartSeries());
		chartPanelHandler = new BarChartPanelHandler(slice.getIntervalStartSeries(), chartPanel.getViewport());
		chartPanelHandler.subscribe();

		return chartPanel;
	}

	private int createSeriesBySlice(SDP2DataSlice<SDP2Key> slice, File root, Scheduler scheduler){
		EditableTSeries<Candle> candleSeries = slice.createSeries(CANDLE_SERIES, true);
		MDStorage<TFSymbol, Candle> mds = null;
		try {
			mds = new FinamData().createCachingOHLCV(root,
					new File(System.getProperty("java.io.tmpdir") + File.separator + "aquila-ohlcv-cache"));
		} catch ( DataStorageException e ) {
			logger.error("Creating storage of historical data failed: ", e);
			return 1;
		}

		try ( CloseableIterator<Candle> it = mds.createReader(new TFSymbol(slice.getSymbol(), slice.getTimeFrame()), 15, scheduler.getCurrentTime()) ) {
			while ( it.next() ) {
				candleSeries.set(it.item().getStartTime(), it.item());
			}
		} catch ( Exception e ) {
			logger.error("Error loading history: ", e);
		}

		TSeries<Double> closeSeries = new CandleCloseTSeries(CANDLE_CLOSE_SERIES, candleSeries);
		TSeries<Double> qema7Series = new QEMATSeries(QEMA7_CANDLE_CLOSE_SERIES, closeSeries, 7);
		TSeries<Double> qema14Series = new QEMATSeries(QEMA14_CANDLE_CLOSE_SERIES, closeSeries, 14);
		TSeries<Long> volumeSeries = new CandleVolumeTSeries(CANDLE_VOLUME_SERIES, candleSeries);
		slice.registerRawSeries(closeSeries);
		slice.registerRawSeries(qema7Series);
		slice.registerRawSeries(qema14Series);
		slice.registerRawSeries(volumeSeries);

		new CandleSeriesByLastTrade(candleSeries, terminal, slice.getSymbol()).start();
		return 0;
	}
}
