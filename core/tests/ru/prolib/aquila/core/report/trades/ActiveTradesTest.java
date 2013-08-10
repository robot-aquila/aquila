package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.RTrade;
import ru.prolib.aquila.core.report.TradeReportEvent;
import ru.prolib.aquila.core.report.trades.ActiveTrades;
import ru.prolib.aquila.core.report.trades.ERTrade;
import ru.prolib.aquila.core.utils.Variant;

public class ActiveTradesTest {
	private static SimpleDateFormat format;
	private static Direction BUY = Direction.BUY;
	@SuppressWarnings("unused")
	private static Direction SELL = Direction.SELL;
	private static SecurityDescriptor descr1, descr2;
	private Trade trade;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType onChanged, onEnter, onExit;
	private ERTrade report1, report2; 
	private Terminal terminal;
	private ActiveTrades reports;
	
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		descr1 = new SecurityDescriptor("Foo", "GZ", "Bar", SecurityType.UNK);
		descr2 = new SecurityDescriptor("Bar", "BZ", "Foo", SecurityType.UNK);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		onChanged = control.createMock(EventType.class);
		onEnter = control.createMock(EventType.class);
		onExit = control.createMock(EventType.class);
		report1 = control.createMock(ERTrade.class);
		report2 = control.createMock(ERTrade.class);
		terminal = control.createMock(Terminal.class);
		reports = new ActiveTrades(dispatcher, onEnter, onExit, onChanged);
		trade = createTrade(descr1, "1999-01-01 00:00:00", BUY, 1L, 1d, 10d);
		expect(report1.getSecurityDescriptor()).andStubReturn(descr1);
		expect(report2.getSecurityDescriptor()).andStubReturn(descr2);
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param time строка yyyy-MM-dd HH:mm:ss время сделки
	 * @param dir направление
	 * @param qty количество
	 * @param price цена
	 * @param volume объем
	 * @return сделка
	 */
	private Trade createTrade(SecurityDescriptor descr, String time,
			Direction dir, Long qty, Double price, Double volume)
		throws Exception
	{
		return createTrade(descr, format.parse(time), dir, qty, price, volume);
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param time время сделки
	 * @param dir направление
	 * @param qty количество
	 * @param price цена
	 * @param volume объем
	 * @return сделка
	 */
	private Trade createTrade(SecurityDescriptor descr, Date time,
			Direction dir, Long qty, Double price, Double volume)
	{
		Trade trade = new Trade(terminal);
		trade.setDirection(dir);
		trade.setPrice(price);
		trade.setQty(qty);
		trade.setSecurityDescriptor(descr);
		trade.setTime(time);
		trade.setVolume(volume);
		return trade;		
	}
	
	@Test
	public void testAddTrade_NewReport() throws Exception {
		RTrade expected = new RTradeImpl(trade);
		dispatcher.dispatch(new TradeReportEvent(onEnter, expected));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertEquals(expected, reports.getReport(descr1));
	}
	
	@Test
	public void testAddTrade_UpdatedReport() throws Exception {
		reports.setReport(descr1, report1);
		expect(report1.addTrade(same(trade))).andReturn(null);
		expect(report1.isOpen()).andReturn(true);
		dispatcher.dispatch(new TradeReportEvent(onChanged, report1));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertSame(report1, reports.getReport(descr1));
	}
	
	@Test
	public void testAddTrade_ExitReport() throws Exception {
		reports.setReport(descr1, report1);
		expect(report1.addTrade(same(trade))).andReturn(null);
		expect(report1.isOpen()).andReturn(false);
		dispatcher.dispatch(new TradeReportEvent(onExit, report1));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertNull(reports.getReport(descr1));
	}

	@Test
	public void testAddTrade_ExitReportWithReverse() throws Exception {
		reports.setReport(descr1, report1);
		expect(report1.addTrade(same(trade))).andReturn(report2);
		expect(report1.isOpen()).andReturn(false);
		dispatcher.dispatch(new TradeReportEvent(onExit, report1));
		dispatcher.dispatch(new TradeReportEvent(onEnter, report2));
		control.replay();
		
		reports.addTrade(trade);
		
		control.verify();
		assertSame(report2, reports.getReport(descr1));
	}
	
	@Test
	public void testGetReports() throws Exception {
		reports.setReport(descr1, report1);
		reports.setReport(descr2, report2);
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
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vEnt = new Variant<EventType>(vDisp)
			.add(onEnter)
			.add(control.createMock(EventType.class));
		Variant<EventType> vExt = new Variant<EventType>(vEnt)
			.add(onExit)
			.add(control.createMock(EventType.class));
		Variant<EventType> vChng = new Variant<EventType>(vExt)
			.add(onChanged)
			.add(control.createMock(EventType.class));
		Variant<List<ERTrade>> vRows =
				new Variant<List<ERTrade>>(vChng)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		control.replay();
		for ( ERTrade r : rows1 ) {
			reports.setReport(r.getSecurityDescriptor(), r);
		}
		int foundCnt = 0;
		ActiveTrades x = null, found = null;
		do {
			x = new ActiveTrades(vDisp.get(), vEnt.get(), vExt.get(),
					vChng.get());
			for ( ERTrade r : vRows.get() ) {
				x.setReport(r.getSecurityDescriptor(), r);
			}
			if ( reports.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onEnter, found.OnEnter());
		assertSame(onExit, found.OnExit());
		assertSame(onChanged, found.OnChanged());
		assertSame(report1, found.getReport(descr1));
		assertSame(report2, found.getReport(descr2));
	}
	
	@Test
	public void testClear() throws Exception {
		reports.setReport(descr1, report1);
		reports.setReport(descr2, report2);
		List<RTrade> expected = new Vector<RTrade>();
		reports.clear();
		
		assertEquals(expected, reports.getReports());
	}
	
	@Test
	public void testConstruct_Min() throws Exception {
		EventDispatcher d =
			new EventDispatcherImpl(new SimpleEventQueue(), "ActiveTrades");
		ActiveTrades expected = new ActiveTrades(d, d.createType("Enter"),
				d.createType("Exit"), d.createType("Changed"));
		
		assertEquals(expected, new ActiveTrades());
	}
	
}
