package ru.prolib.aquila.utils.experimental.sst;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerControlToolbar;
import ru.prolib.aquila.probe.scheduler.ui.SchedulerTaskFilter;
import ru.prolib.aquila.probe.scheduler.ui.SymbolUpdateTaskFilter;
import ru.prolib.aquila.probe.scheduler.utils.EventQueueSynchronizer;
import ru.prolib.aquila.qforts.impl.QFTransactionException;
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
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.swing.BarChartPanelImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerSetup;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWALOLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWCandlestickLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWOELayer;
import ru.prolib.aquila.utils.experimental.sst.msig.BreakSignal;
import ru.prolib.aquila.utils.experimental.sst.robot.DataServiceLocator;
import ru.prolib.aquila.utils.experimental.sst.robot.Robot;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotBuilder;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotConfig;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotState;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotDataSliceTracker;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotStateListener;
import ru.prolib.aquila.utils.experimental.sst.robot.RobotStateListenerStub;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSlice;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;

/**
 * TODO:
 * 
 * 2018-04-02 - Using global exit signal does not demonstrate how to do safe shutdown. 
 */
public class SecuritySimulationTest implements Experiment, RobotStateListener {
	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SecuritySimulationTest.class);
	}
	
	private final long startTime = System.currentTimeMillis();
	private JTabbedPane tabPanel;
	private DataServiceLocator serviceLocator = new DataServiceLocator();
	private BreakSignal breakSignal;
	private Robot robot;
	private BarChartPanelImpl chartPanel;

	@Override
	public void close() throws IOException {
		if ( breakSignal != null ) {
			breakSignal.fireBreak();
			breakSignal = null;
		}
		serviceLocator.getTerminal().stop();
	}

	@Override
	public int run(Scheduler scheduler, CommandLine cmd, CountDownLatch exitSignal) {
		// Prepare robot config
		final RobotConfig rConfig = new RobotConfig(new Symbol(cmd.getOptionValue(CmdLine.LOPT_SYMBOL, "Si-9.16")),
				new Account("TEST-ACCOUNT"),
				CmdLine.getTimeFrame(cmd, ZONE_ID),
				0.5d);
		logger.debug("Selected strategy symbol: {}", rConfig.getSymbol());
		logger.debug("Selected timeframe: {}", rConfig.getTFrame());
		
		// Prepare service locator
		serviceLocator.setDataRootDirectory(new File(cmd.getOptionValue(CmdLine.LOPT_ROOT)));
		serviceLocator.setScheduler(scheduler);
		
		// Initialize a test account
		try {
			serviceLocator.getQFortsEnv().createPortfolio(new Account("TEST-ACCOUNT"), CDecimalBD.ofRUB2("300000"));
		} catch ( QFTransactionException e ) {
			logger.error("Error creating test portfolio: ", e);
			return 1;
		}
		
		// Make a break signal
		breakSignal = new BreakSignal(serviceLocator.getTerminal().getEventQueue(), "GLOBAL");
		
		// Prepare symbol list
		Collection<Symbol> symbols = null;
		try {
			symbols = Collections.unmodifiableCollection(serviceLocator.getContractDataStorage().getSymbols());
		} catch ( DataStorageException e ) {
			logger.error("Error reading symbol list: ", e);
			return 1;
		}
		final Collection<Symbol> symbols_dup = symbols;

		// Subscribe for all symbols
		final Terminal terminal = serviceLocator.getTerminal();
		terminal.onTerminalReady().listenOnce(new EventListener() {
			@Override
			public void onEvent(Event event) {
				Instant t = terminal.getCurrentTime();
				logger.debug("Terminal ready at {}", t);
				for ( Symbol symbol : symbols_dup ) {
					if ( rConfig.getSymbol().equals(symbol) ) {
						terminal.subscribe(symbol);
						logger.debug("Subscribed for: {}", symbol);
					}
				}
				logger.debug("Total symbols: {}", symbols_dup.size());
			}
		});

		// PROBE scheduler special options processing
		boolean isAutoShutdown = false, isAutoStart = false;
		if ( scheduler.getClass() == SchedulerImpl.class ) {
			final SchedulerImpl s = (SchedulerImpl) scheduler;
			s.addSynchronizer(new EventQueueSynchronizer(serviceLocator.getTerminal().getEventQueue()));
			
			if ( cmd.hasOption(CmdLine.LOPT_PROBE_SCHEDULER_AUTOSHUTDOWN) ) {
				isAutoShutdown = true;
				String astString = cmd.getOptionValue(CmdLine.LOPT_PROBE_SCHEDULER_AUTOSHUTDOWN);
				Instant ast = null;
				try {
					ast = Instant.parse(astString);
				} catch ( DateTimeParseException e ) {
					logger.error("Invalid autoshutdown time: {}", astString);
					return 1;
				}
				scheduler.schedule(new Runnable() {
					@Override
					public void run() {
						exitSignal.countDown();
					}
				}, ast);
			} else if ( cmd.hasOption(CmdLine.LOPT_PROBE_SCHEDULER_AUTOSTOP) ) {
				String astString = cmd.getOptionValue(CmdLine.LOPT_PROBE_SCHEDULER_AUTOSTOP);
				Instant ast = null;
				try {
					ast = Instant.parse(astString);
				} catch ( DateTimeParseException e ) {
					logger.error("Invalid autostop time: {}", astString);
					return 1;
				}
				scheduler.schedule(new Runnable() {
					@Override
					public void run() {
						s.setModeWait();
					}
				}, ast);
				
			}
			
			if ( cmd.hasOption(CmdLine.LOPT_PROBE_SCHEDULER_AUTOSTART) ) {
				isAutoStart = true;
				s.setExecutionSpeed(0);
				terminal.onTerminalReady().listenOnce(new EventListener() {
					@Override
					public void onEvent(Event event) {
						s.setModeRun();
					}
				});
			}

		}
		
		boolean isHeadless = false;
		if ( cmd.hasOption(CmdLine.LOPT_HEADLESS) ) {
			if ( ! isAutoShutdown || ! isAutoStart ) {
				logger.error("This experiment can be run headless mode only in combination with autostart and autoshutdown options");
				return 2;
			}
			isHeadless = true;
		}
		
		if ( ! isHeadless ) {
			// It is better to initialize main UI before starting terminal
			// because terminal queue thread may be faster than this thread.
			// But robot UI requires elements of main UI and if it is not created
			// prior to running robot it may cause NPE exceptions.
			createTerminalUI(exitSignal);
		}
		
		terminal.start(); 
		
		robot = new RobotBuilder(serviceLocator, breakSignal).buildBullDummy(rConfig);
		robot.getData().setStateListener(isHeadless ? new RobotStateListenerStub() : this);
		robot.getAutomat().setDebug(true);
		try {
			robot.getAutomat().start();
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
			return 2;
		}
		
		return 0;
	}
	
	@Override
	public int getExitCode() {
		long timeUsed = System.currentTimeMillis() - startTime;
		logger.debug("Time used: {} seconds", timeUsed / 1000);
		return 0;
	}
	
	private void createTerminalUI(CountDownLatch exitSignal) {
		final IMessages messages = new Messages();
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
		if ( serviceLocator.getScheduler().getClass() == SchedulerImpl.class ) {
			SchedulerImpl s = (SchedulerImpl) serviceLocator.getScheduler();
			List<SchedulerTaskFilter> filters = new ArrayList<>();
			filters.add(new SymbolUpdateTaskFilter(messages));
			topPanel.add(new SchedulerControlToolbar(messages, s, ZONE_ID, filters));
		}
		Terminal terminal = serviceLocator.getTerminal();

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
	}

	private void createRobotUI() {
		RobotState robotData = robot.getData();
		RobotDataSliceTracker rdsTracker = robotData.getDataSliceTracker();
		SDP2DataSlice<SDP2Key> slice = rdsTracker.getDataSlice();
		ObservableTSeries<Instant> categories = slice.getIntervalStartSeries();
		Symbol symbol = slice.getSymbol();

		chartPanel = new BarChartPanelImpl(BarChartOrientation.LEFT_TO_RIGHT);
		// Setup category axis ruler renderers
		CategoryAxisDriver cad = chartPanel.getCategoryAxisDriver();
		cad.registerRenderer(new SWTimeAxisRulerRenderer("TIME", categories));
		
		BarChart chart = chartPanel.addChart("CANDLES")
				.setHeight(600)
				.addStaticOverlay(symbol + ", " + slice.getTimeFrame().toTFrame(), 0);
		ChartSpaceManager vsm = chart.getVerticalSpaceManager();
		vsm.getGridLinesSetup("CATEGORY", "TIME").setVisible(true);
		// TODO: add date ruler to top
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			.setVisible(true)
			.setShowInnerLine(true)
			.setShowOuterLine(false);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setVisible(false);
		
		// Setup value axis ruler renderers
		ValueAxisDriver vad = chart.getValueAxisDriver();
		((SWValueAxisRulerRenderer) vad.getRenderer("LABEL")).setTickSize(robotData.getSecurity().getTickSize());
		ChartSpaceManager hsm = chart.getHorizontalSpaceManager();
		hsm.getGridLinesSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);
		
		chart.addLayer(new SWCandlestickLayer(slice.getSeries(RobotDataSliceTracker.CANDLE_SERIES)));
		chart.addSmoothLine(slice.getSeries(RobotDataSliceTracker.QEMA7_CANDLE_CLOSE_SERIES)).setColor(Color.BLUE);
		chart.addSmoothLine(slice.getSeries(RobotDataSliceTracker.QEMA14_CANDLE_CLOSE_SERIES)).setColor(Color.MAGENTA);
		//chart.addLayer(new BarChartCurrentValueLayer(slice.getSeries(CANDLE_CLOSE_SERIES))); // TODO: fix me
		chart.addLayer(new SWALOLayer(RobotDataSliceTracker.ACTIVE_ORDERS, rdsTracker.getALODataProvider()));
		chart.addLayer(new SWOELayer(slice.getSeries(RobotDataSliceTracker.ORDER_EXECUTION_SERIES)));
		
		chart = chartPanel.addChart("VOLUMES")
				.setHeight(200)
				//.setZeroAtCenter(true)
				.addStaticOverlay("Volume", 0);
		chart.addHistogram(slice.getSeries(RobotDataSliceTracker.CANDLE_VOLUME_SERIES));
		vsm = chart.getVerticalSpaceManager();
		((SWTimeAxisRulerSetup) vsm.getLowerRulerSetup("CATEGORY", "TIME"))
			.setVisible(true)
			.setDisplayPriority(20) // hide this first
			.setShowInnerLine(true)
			.setShowOuterLine(true);
		((SWTimeAxisRulerSetup) vsm.getUpperRulerSetup("CATEGORY", "TIME"))
			.setVisible(true)
			.setDisplayPriority(10)
			.setShowInnerLine(true)
			.setShowOuterLine(false);
		// TODO: add date ruler to bottom
		
		vad = chart.getValueAxisDriver();
		((SWValueAxisRulerRenderer) vad.getRenderer("LABEL")).setTickSize(CDecimalBD.of(1L));
		hsm = chart.getHorizontalSpaceManager();
		hsm.getLowerRulerSetup("VALUE", "LABEL").setVisible(true);
		hsm.getUpperRulerSetup("VALUE", "LABEL").setVisible(true);
		
		// Add chart to tab panel
		chartPanel.setCategories(categories);
		JPanel chartRoot = new JPanel(new GridLayout(1, 1));
		chartRoot.add(chartPanel.getRootPanel());
		chartPanel.setPreferredNumberOfBars(100);
		tabPanel.addTab("Strategy: " + robotData.getMarketSignal().getID(), chartRoot);
	}
	
	private void destroyRobotUI() {
		BarChart chart = chartPanel.getChart("CANDLES");
		chart.dropLayer(RobotDataSliceTracker.CANDLE_SERIES);
		chart.dropLayer(RobotDataSliceTracker.QEMA7_CANDLE_CLOSE_SERIES);
		chart.dropLayer(RobotDataSliceTracker.QEMA14_CANDLE_CLOSE_SERIES);
		chart.dropLayer(RobotDataSliceTracker.ACTIVE_ORDERS);
		chart.dropLayer(RobotDataSliceTracker.ORDER_EXECUTION_SERIES);
		// TODO: dropLayers
		
		chart = chartPanel.getChart("VOLUMES");
		chart.dropLayer(RobotDataSliceTracker.CANDLE_VOLUME_SERIES);
		// TODO: dropLayers
		
		chartPanel.paint(); // refresh chart
	}

	@Override
	public void robotStarted() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					robotStarted();
				}
			});
		} else {
			createRobotUI();
		}
	}

	@Override
	public void robotStopped() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					robotStopped();
				}
			});
		} else {
			destroyRobotUI();
		}
	}

}
