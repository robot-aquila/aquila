package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class ActiveTradeReportsTest {
	private static SimpleDateFormat format;
	private static OrderDirection BUY = OrderDirection.BUY;
	private static OrderDirection SELL = OrderDirection.SELL;
	private static SecurityDescriptor descr1, descr2;
	private Trade trade;
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType onChanged, onEnter, onExit;
	private EditableTradeReport report1, report2; 
	private Terminal terminal;
	private ActiveTradeReports reports;
	
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
		report1 = control.createMock(EditableTradeReport.class);
		report2 = control.createMock(EditableTradeReport.class);
		terminal = control.createMock(Terminal.class);
		reports = new ActiveTradeReports(dispatcher, onEnter, onExit, onChanged);
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
			OrderDirection dir, Long qty, Double price, Double volume)
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
			OrderDirection dir, Long qty, Double price, Double volume)
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
		TradeReport expected = new TradeReportImpl(trade);
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
		report1 = new TradeReportImpl(trade);
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
		report2 = new TradeReportImpl(createTrade(descr2, "1998-01-01 00:00:00",
				SELL, 1L, 1d, 1d));
		reports.setReport(descr1, report1);
		reports.setReport(descr2, report2);
		List<TradeReport> expected = new Vector<TradeReport>();
		expected.add(report2);
		expected.add(report1);
		
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
		List<EditableTradeReport> rows1 = new Vector<EditableTradeReport>();
		rows1.add(report1);
		rows1.add(report2);
		List<EditableTradeReport> rows2 = new Vector<EditableTradeReport>();
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
		Variant<List<EditableTradeReport>> vRows =
				new Variant<List<EditableTradeReport>>(vChng)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		control.replay();
		for ( EditableTradeReport r : rows1 ) {
			reports.setReport(r.getSecurityDescriptor(), r);
		}
		int foundCnt = 0;
		ActiveTradeReports x = null, found = null;
		do {
			x = new ActiveTradeReports(vDisp.get(), vEnt.get(), vExt.get(),
					vChng.get());
			for ( EditableTradeReport r : vRows.get() ) {
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
	
}
