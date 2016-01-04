package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.*;
import ru.prolib.aquila.core.utils.Variant;

public class ActiveTradesTest {
	private static Direction BUY = Direction.BUY;
	private static Symbol symbol1, symbol2;
	private EventSystem es;
	private Trade trade;
	private IMocksControl control;
	private ActiveTradesEventDispatcher dispatcher;
	private ERTrade report1, report2; 
	private Terminal terminal;
	private ActiveTrades reports;
	
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		symbol1 = new Symbol("Foo", "GZ", "USD", SymbolType.UNKNOWN);
		symbol2 = new Symbol("Bar", "BZ", "RUB", SymbolType.UNKNOWN);
	}
	
	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		control = createStrictControl();
		dispatcher = control.createMock(ActiveTradesEventDispatcher.class);
		report1 = control.createMock(ERTrade.class);
		report2 = control.createMock(ERTrade.class);
		terminal = control.createMock(Terminal.class);
		reports = new ActiveTrades(dispatcher);
		trade = createTrade(symbol1, "1999-01-01T00:00:00Z", BUY, 1L, 1d, 10d);
		expect(report1.getSymbol()).andStubReturn(symbol1);
		expect(report2.getSymbol()).andStubReturn(symbol2);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @param time строка yyyy-MM-dd HH:mm:ss время сделки
	 * @param dir направление
	 * @param qty количество
	 * @param price цена
	 * @param volume объем
	 * @return сделка
	 */
	private Trade createTrade(Symbol symbol, String time,
			Direction dir, Long qty, Double price, Double volume)
		throws Exception
	{
		return createTrade(symbol, Instant.parse(time), dir, qty,
				price, volume);
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param symbol дескриптор инструмента
	 * @param time время сделки
	 * @param dir направление
	 * @param qty количество
	 * @param price цена
	 * @param volume объем
	 * @return сделка
	 */
	private Trade createTrade(Symbol symbol, Instant time,
			Direction dir, Long qty, Double price, Double volume)
	{
		Trade trade = new Trade(terminal);
		trade.setDirection(dir);
		trade.setPrice(price);
		trade.setQty(qty);
		trade.setSymbol(symbol);
		trade.setTime(time);
		trade.setVolume(volume);
		return trade;		
	}
	
	@Test
	public void testAddTrade_NewReport() throws Exception {
		RTrade expected = new RTradeImpl(trade);
		dispatcher.fireEnter(eq(expected));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertEquals(expected, reports.getReport(symbol1));
	}
	
	@Test
	public void testAddTrade_UpdatedReport() throws Exception {
		reports.setReport(symbol1, report1);
		expect(report1.addTrade(same(trade))).andReturn(null);
		expect(report1.isOpen()).andReturn(true);
		dispatcher.fireChanged(same(report1));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertSame(report1, reports.getReport(symbol1));
	}
	
	@Test
	public void testAddTrade_ExitReport() throws Exception {
		reports.setReport(symbol1, report1);
		expect(report1.addTrade(same(trade))).andReturn(null);
		expect(report1.isOpen()).andReturn(false);
		dispatcher.fireExit(same(report1));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertNull(reports.getReport(symbol1));
	}

	@Test
	public void testAddTrade_ExitReportWithReverse() throws Exception {
		reports.setReport(symbol1, report1);
		expect(report1.addTrade(same(trade))).andReturn(report2);
		expect(report1.isOpen()).andReturn(false);
		dispatcher.fireExit(same(report1));
		dispatcher.fireEnter(same(report2));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertSame(report2, reports.getReport(symbol1));
	}
	
	@Test
	public void testGetReports() throws Exception {
		reports.setReport(symbol1, report1);
		reports.setReport(symbol2, report2);
		List<RTrade> expected = new Vector<RTrade>();
		expected.add(report1);
		expected.add(report2);
		
		assertEquals(expected, reports.getReports());
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(reports.equals(reports));
		assertFalse(reports.equals(null));
		assertFalse(reports.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<ERTrade> rows1 = new Vector<ERTrade>();
		rows1.add(report1);
		rows1.add(report2);
		List<ERTrade> rows2 = new Vector<ERTrade>();
		rows2.add(report1);
		Variant<List<ERTrade>> vRows = new Variant<List<ERTrade>>()
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		control.replay();
		for ( ERTrade r : rows1 ) {
			reports.setReport(r.getSymbol(), r);
		}
		int foundCnt = 0;
		ActiveTrades x = null, found = null;
		do {
			// Объекты системы событий не участвуют в сравнении
			x = new ActiveTrades(es);
			for ( ERTrade r : vRows.get() ) {
				x.setReport(r.getSymbol(), r);
			}
			if ( reports.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(report1, found.getReport(symbol1));
		assertSame(report2, found.getReport(symbol2));
	}
	
	@Test
	public void testClear() throws Exception {
		reports.setReport(symbol1, report1);
		reports.setReport(symbol2, report2);
		List<RTrade> expected = new Vector<RTrade>();
		reports.clear();
		
		assertEquals(expected, reports.getReports());
	}
	
	@Test
	public void testConstruct_Min() throws Exception {
		ActiveTrades expected =
			new ActiveTrades(new ActiveTradesEventDispatcher(es));
		
		assertTrue(expected.equals(new ActiveTrades(es)));
	}
	
}
