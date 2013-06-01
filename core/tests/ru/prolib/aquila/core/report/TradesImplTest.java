package ru.prolib.aquila.core.report;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.junit.*;

import com.csvreader.CsvReader;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

public class TradesImplTest {
	private static final String TIME = "TIME";
	private static final String SEC_CODE = "SEC_CODE";
	private static final String DIR = "DIR";
	private static final String PRICE = "PRICE";
	private static final String QTY = "QTY";
	private static final String VOL = "VOL";
	private static final String ID = "ID";
	private static final String ORD_ID = "ORD_ID";
	private static final String TYPE = "TYPE";
	private static final String ENTER_TIME = "EN_TIME";
	private static final String ENTER_PRICE = "EN_PRICE";
	private static final String ENTER_VOL = "EN_VOL";
	private static final String ENTER_QTY = "EN_QTY";
	private static final String EXIT_QTY = "EX_QTY";
	private static final String EXIT_TIME = "EX_TIME";
	private static final String EXIT_PRICE = "EX_PRICE";
	private static final String EXIT_VOL = "EX_VOL";
	private static final String INDEX = "INDEX";
	private static final String EVT_TYPE = "EVT_TYPE";
	private static final String DIR_BUY = "B";
	private static final String LONG = "LONG";
	private static SimpleDateFormat timeFormat;
	
	private EditableTerminal terminal;
	private EventSystem es;
	private ActiveTrades activeTrades;
	private EventDispatcher dispatcher;
	private EventType onEnter, onExit, onChanged;
	private TradesImpl trades;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		terminal = new TerminalBuilder().createTerminal("test");
		es = terminal.getEventSystem();
		EventDispatcher ad = new EventDispatcherImpl(new SimpleEventQueue());
		activeTrades = new ActiveTrades(ad, ad.createType("Enter"),
				ad.createType("Exit"), ad.createType("Changed"));
		dispatcher = es.createEventDispatcher("Trades");
		onEnter = dispatcher.createType("Enter");
		onExit = dispatcher.createType("Exit");
		onChanged = dispatcher.createType("Changed");
		trades = new TradesImpl(activeTrades, dispatcher,
				onEnter, onExit, onChanged);
		es.getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		stopQueue();
	}
	
	private void stopQueue() throws Exception {
		if ( es.getEventQueue().started() ) {
			es.getEventQueue().stop();
			es.getEventQueue().join(1000L);
		}
	}
	
	/**
	 * Загрузить сделки из файла.
	 * <p>
	 * @param dest целевой экземпляр набора трейдов
	 * @param filename имя файла с описанием сделок
	 */
	private void loadTrades(TradesImpl dest, String filename) throws Exception {
		for ( Trade trade : loadTrades(filename) ) {
			dest.addTrade(trade);
		}
	}
	
	/**
	 * Загрузить сделки из файла.
	 * <p>
	 * @param filename имя файла с описанием сделок
	 * @return список загруженных сделок
	 */
	private List<Trade> loadTrades(String filename) throws Exception {
		CsvReader reader = new CsvReader(getFullPath(filename));
		List<Trade> list = new Vector<Trade>();
		reader.readHeaders();
		while ( reader.readRecord() ) {
			Trade trade = new Trade(terminal);
			trade.setDirection(DIR_BUY.equals(reader.get(DIR))
					? OrderDirection.BUY : OrderDirection.SELL);
			trade.setId(Long.parseLong(reader.get(ID)));
			trade.setOrderId(Long.parseLong(reader.get(ORD_ID)));
			trade.setPrice(Double.parseDouble(reader.get(PRICE)));
			trade.setQty(Long.parseLong(reader.get(QTY)));
			trade.setSecurityDescriptor(getSecDescr(reader));
			trade.setTime(timeFormat.parse(reader.get(TIME)));
			trade.setVolume(Double.parseDouble(reader.get(VOL)));
			list.add(trade);
		}
		reader.close();
		return list;
	}
	
	/**
	 * Загрузить отчеты по трейдам из файла.
	 * <p>
	 * @param filename имя файла с описанием трейдов
	 * @return список загруженных отчетов
	 */
	private List<TradeReport> loadTradeReports(String filename)
		throws Exception
	{
		CsvReader reader = new CsvReader(getFullPath(filename));
		List<TradeReport> list = new Vector<TradeReport>();
		reader.readHeaders();
		while ( reader.readRecord() ) {
			list.add(loadTradeReport(reader));
		}
		reader.close();
		return list;
	}
	
	private List<TradeReportEvent> loadTradeReportEvents(String filename)
		throws Exception
	{
		CsvReader reader = new CsvReader(getFullPath(filename));
		Map<String, EventType> evtMap = new Hashtable<String, EventType>();
		evtMap.put("ENTER", onEnter);
		evtMap.put("CHANGE", onChanged);
		evtMap.put("EXIT", onExit);
		List<TradeReportEvent> list = new Vector<TradeReportEvent>();
		reader.readHeaders();
		while ( reader.readRecord() ) {
			list.add(new TradeReportEvent(evtMap.get(reader.get(EVT_TYPE)),
					loadTradeReport(reader),
					Integer.parseInt(reader.get(INDEX))));
		}
		reader.close();
		return list;
	}
	
	/**
	 * Загрузить отчет по трейдам из текущей позиции ридера.
	 * <p>
	 * @param reader источник данных
	 * @return отчет по трейдам
	 */
	private TradeReport loadTradeReport(CsvReader reader) throws Exception {
		String exTime = reader.get(EXIT_TIME);
		String exQty = reader.get(EXIT_QTY);
		String exPrice = reader.get(EXIT_PRICE);
		String exVol = reader.get(EXIT_VOL);
		TradeReport report = new TradeReportImpl(getSecDescr(reader),
			(LONG.equals(reader.get(TYPE))
				? PositionType.LONG : PositionType.SHORT),
			timeFormat.parse(reader.get(ENTER_TIME)),
			(exTime.equals("") ? null : timeFormat.parse(exTime)),
			Long.parseLong(reader.get(ENTER_QTY)),
			(exQty.equals("") ? null : Long.parseLong(exQty)),
			Double.parseDouble(reader.get(ENTER_PRICE)),
			(exPrice.equals("") ? null : Double.parseDouble(exPrice)),
			Double.parseDouble(reader.get(ENTER_VOL)),
			(exVol.equals("") ? null : Double.parseDouble(exVol)));
		return report;
	}
	
	/**
	 * Сформировать дескриптор инструмента.
	 * <p>
	 * @param reader источник данных
	 * @return дескриптор инструмента
	 */
	private SecurityDescriptor getSecDescr(CsvReader reader) throws Exception {
		return new SecurityDescriptor(reader.get(SEC_CODE),
				"TEST", "USD", SecurityType.FUT);
	}
	
	/**
	 * Получить полный путь к файлу фикстуры.
	 * <p>
	 * @param filename имя файла
	 * @return полный путь к файлу
	 */
	private String getFullPath(String filename) {
		String s = System.getProperty("file.separator");
		return System.getProperty("user.dir") + s + "fixture" + s + filename;
	}
	
	@Test
	public void testStart() throws Exception {
		trades.start();
		
		assertTrue(activeTrades.OnEnter().isListener(trades));
		assertTrue(activeTrades.OnExit().isListener(trades));
		assertTrue(activeTrades.OnChanged().isListener(trades));
	}
	
	@Test
	public void testStop() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		trades.stop();
		assertFalse(activeTrades.OnEnter().isListener(trades));
		assertFalse(activeTrades.OnExit().isListener(trades));
		assertFalse(activeTrades.OnChanged().isListener(trades));
		assertEquals(new Vector<TradeReport>(), activeTrades.getReports());
		assertEquals(new Vector<TradeReport>(), trades.getTradeReports());
	}
	
	@Test
	public void testGetTradeReportCount() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		assertEquals(5, trades.getTradeReportCount());
	}
	
	@Test
	public void testGetTradeReports() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		List<TradeReport> expected = loadTradeReports("trade-reports1.csv");
		assertEquals(expected, trades.getTradeReports());
	}
	
	@Test
	public void testGetTradeReport() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		List<TradeReport> expected = loadTradeReports("trade-reports1.csv");
		assertEquals(expected.get(1), trades.getTradeReport(1));
		assertEquals(expected.get(4), trades.getTradeReport(4));
	}
	
	@Test
	public void testEventHandling() throws Exception {
		final List<Event> actual = new Vector<Event>();
		final EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				System.err.println("ABC");
				actual.add(event);
			}
		};
		trades.start();
		trades.OnChanged().addListener(listener);
		trades.OnEnter().addListener(listener);
		trades.OnExit().addListener(listener);
		loadTrades(trades, "trades1.csv");
		System.err.println(es.getEventQueue().started());
		stopQueue();
		//Thread.sleep(2000);
		
		List<TradeReportEvent> expected =
			loadTradeReportEvents("trade-reports1-evts.csv");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(trades.equals(trades));
		assertFalse(trades.equals(null));
		assertFalse(trades.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	public void test_() throws Exception {
		fail("TODO: incomplete");
	}

}
