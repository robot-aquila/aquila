package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;
import com.csvreader.CsvReader;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.*;
import ru.prolib.aquila.core.utils.Variant;

public class CommonTRTest {
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
	private static SecurityDescriptor descr;
	
	private IMocksControl control;
	private EditableTerminal<?> terminal;
	private EventSystem es;
	private CommonTREventDispatcher dispatcher;
	private CommonTR trades;
	private ActiveTrades activeTrades;
	private RTrade record;
	private Security security;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		descr = new SecurityDescriptor("RI", "SPFB", "USD", SecurityType.FUT);
	}

	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		activeTrades = control.createMock(ActiveTrades.class);
		record = control.createMock(RTrade.class);
		security = control.createMock(Security.class);
		terminal = new TerminalImpl("test");
		es = terminal.getEventSystem();
		dispatcher = new CommonTREventDispatcher(es);
		trades = new CommonTR(es, dispatcher);
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
	private void loadTrades(CommonTR dest, String filename) throws Exception {
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
					? Direction.BUY : Direction.SELL);
			trade.setId(Long.parseLong(reader.get(ID)));
			trade.setOrderId(Long.parseLong(reader.get(ORD_ID)));
			trade.setPrice(Double.parseDouble(reader.get(PRICE)));
			trade.setQty(Long.parseLong(reader.get(QTY)));
			trade.setSecurityDescriptor(getSecDescr(reader));
			trade.setTime(new DateTime(timeFormat.parse(reader.get(TIME))));
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
	private List<RTrade> loadTradeReports(String filename)
		throws Exception
	{
		CsvReader reader = new CsvReader(getFullPath(filename));
		List<RTrade> list = new Vector<RTrade>();
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
		Map<String, EventTypeSI> evtMap = new Hashtable<String, EventTypeSI>();
		evtMap.put("ENTER", (EventTypeSI) dispatcher.OnEnter());
		evtMap.put("CHANGE", (EventTypeSI) dispatcher.OnChanged());
		evtMap.put("EXIT", (EventTypeSI) dispatcher.OnExit());
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
	private RTrade loadTradeReport(CsvReader reader) throws Exception {
		String exTime = reader.get(EXIT_TIME);
		String exQty = reader.get(EXIT_QTY);
		String exPrice = reader.get(EXIT_PRICE);
		String exVol = reader.get(EXIT_VOL);
		RTrade report = new RTradeImpl(getSecDescr(reader),
			(LONG.equals(reader.get(TYPE))
				? PositionType.LONG : PositionType.SHORT),
			new DateTime(timeFormat.parse(reader.get(ENTER_TIME))),
			(exTime.equals("") ? null : new DateTime(timeFormat.parse(exTime))),
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
		
		ActiveTrades activeTrades = trades.getActiveTrades();
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
		ActiveTrades activeTrades = trades.getActiveTrades();
		assertFalse(activeTrades.OnEnter().isListener(trades));
		assertFalse(activeTrades.OnExit().isListener(trades));
		assertFalse(activeTrades.OnChanged().isListener(trades));
		assertEquals(new Vector<RTrade>(), activeTrades.getReports());
		assertEquals(new Vector<RTrade>(), trades.getRecords());
	}
	
	@Test
	public void testSize() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		assertEquals(5, trades.size());
	}
	
	@Test
	public void testGetRecords() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		List<RTrade> expected = loadTradeReports("trade-reports1.csv");
		assertEquals(expected, trades.getRecords());
	}
	
	@Test
	public void testGetRecord() throws Exception {
		trades.start();
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
		List<RTrade> expected = loadTradeReports("trade-reports1.csv");
		assertEquals(expected.get(1), trades.getRecord(1));
		assertEquals(expected.get(4), trades.getRecord(4));
	}
	
	@Test
	public void testEventHandling() throws Exception {
		final List<Event> actual = new Vector<Event>();
		final EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		};
		trades.start();
		trades.OnChanged().addListener(listener);
		trades.OnEnter().addListener(listener);
		trades.OnExit().addListener(listener);
		loadTrades(trades, "trades1.csv");
		stopQueue();
		
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
		trades = new CommonTR(es, dispatcher);
		
		Variant<String> vTrdFile = new Variant<String>()
			.add("trades1.csv")
			.add("trades2.csv");
		Variant<?> iterator = vTrdFile;
		trades.start();
		loadTrades(trades, "trades1.csv");
		int foundCnt = 0;
		CommonTR x = null, found = null;
		do {
			x = new CommonTR(es, dispatcher);
			x.start();
			loadTrades(x, vTrdFile.get());
			if ( trades.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(trades.getActiveTrades(), found.getActiveTrades());
		assertEquals(loadTradeReports("trade-reports1.csv"),found.getRecords());
	}
	
	@Test
	public void testGetCurrent_SD() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(activeTrades.getReport(descr)).andReturn(record);
		control.replay();
		
		assertSame(record, trades.getCurrent(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetCurrent_S() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(security.getDescriptor()).andReturn(descr);
		expect(activeTrades.getReport(descr)).andReturn(record);
		control.replay();
		
		assertSame(record, trades.getCurrent(security));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_SD_Zero() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(activeTrades.getReport(descr)).andReturn(null);
		control.replay();
		
		assertEquals(0L, trades.getPosition(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_SD_Long() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(activeTrades.getReport(descr)).andReturn(record);
		expect(record.getType()).andReturn(PositionType.LONG);
		expect(record.getUncoveredQty()).andReturn(10L);
		control.replay();
		
		assertEquals(10L, trades.getPosition(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_SD_Short() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(activeTrades.getReport(descr)).andReturn(record);
		expect(record.getType()).andReturn(PositionType.SHORT);
		expect(record.getUncoveredQty()).andReturn(5L);
		control.replay();
		
		assertEquals(-5L, trades.getPosition(descr));
		
		control.verify();
	}

	@Test
	public void testGetPosition_S_Zero() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(security.getDescriptor()).andReturn(descr);
		expect(activeTrades.getReport(descr)).andReturn(null);
		control.replay();
		
		assertEquals(0L, trades.getPosition(security));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_S_Long() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(security.getDescriptor()).andReturn(descr);
		expect(activeTrades.getReport(descr)).andReturn(record);
		expect(record.getType()).andReturn(PositionType.LONG);
		expect(record.getUncoveredQty()).andReturn(10L);
		control.replay();
		
		assertEquals(10L, trades.getPosition(security));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_S_Short() throws Exception {
		dispatcher = control.createMock(CommonTREventDispatcher.class);
		trades = new CommonTR(dispatcher, activeTrades);
		expect(security.getDescriptor()).andReturn(descr);
		expect(activeTrades.getReport(descr)).andReturn(record);
		expect(record.getType()).andReturn(PositionType.SHORT);
		expect(record.getUncoveredQty()).andReturn(5L);
		control.replay();
		
		assertEquals(-5L, trades.getPosition(security));
		
		control.verify();
	}

}
