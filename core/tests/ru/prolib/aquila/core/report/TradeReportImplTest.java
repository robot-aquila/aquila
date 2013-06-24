package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class TradeReportImplTest {
	private static SimpleDateFormat format;
	private static PositionType LONG = PositionType.LONG;
	private static PositionType SHORT = PositionType.SHORT;
	private static OrderDirection BUY = OrderDirection.BUY;
	private static OrderDirection SELL = OrderDirection.SELL;
	private static SecurityDescriptor descr1, descr2;
	private static Date time1, time2;
	private IMocksControl control;
	private Terminal terminal;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		descr1 = new SecurityDescriptor("Foo", "Bar", "USD", SecurityType.UNK);
		descr2 = new SecurityDescriptor("Bar", "Buz", "SUR", SecurityType.FUT);
		time1 = format.parse("2013-01-01 00:00:00");
		time2 = format.parse("2013-01-02 00:00:00");
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
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

	/**
	 * Вспомогательный класс строки фикстуры.
	 */
	private static class FR {
		/**
		 * Список сделок для добавления в отчет. Первая сделка для создания.
		 */
		private final List<Trade> trades;
		/**
		 * Ожидаемое состояние на момент добавления последней сделки.
		 */
		private final EditableTradeReport expected;
		/**
		 * Ожидаемый результат возврата на добавление последней сделки.
		 */
		private final EditableTradeReport result;
		
		private FR(EditableTradeReport expected, EditableTradeReport result) {
			super();
			this.expected = expected;
			this.result = result;
			trades = new Vector<Trade>();
		}
		
	}
	
	@Test
	public void testConstructor1() throws Exception {
		Trade trade = createTrade(descr1, time1, BUY, 50L, 10.0d, 1000.0d);
		TradeReportImpl report = new TradeReportImpl(trade);
		assertNull(report.getExitPrice());
		assertEquals(10.0d, report.getEnterPrice(), 0.01d);
		assertNull(report.getExitTime());
		assertEquals(time1, report.getEnterTime());
		assertEquals(1000.0d, report.getEnterVolume(), 0.01d);
		assertEquals(new Long(50L), report.getQty());
		assertEquals(descr1, report.getSecurityDescriptor());
		assertEquals(LONG, report.getType());
		assertEquals(new Long(50L), report.getUncoveredQty());
		assertTrue(report.isOpen());
	}
	
	@Test
	public void testConstruct10() throws Exception {
		TradeReportImpl report = new TradeReportImpl(descr2, LONG, time1, time2,
				10L, 5L, 120.35d, 60.15d, 200.0d, 100.0d);
		assertEquals(12.03d, report.getExitPrice(), 0.001d);
		assertEquals(12.035d, report.getEnterPrice(), 0.001d);
		assertEquals(time2, report.getExitTime());
		assertEquals(time1, report.getEnterTime());
		assertEquals(200.0d, report.getEnterVolume(), 0.001d);
		assertEquals(new Long(10L), report.getQty());
		assertEquals(descr2, report.getSecurityDescriptor());
		assertEquals(LONG, report.getType());
		assertEquals(new Long(5L), report.getUncoveredQty());
		assertTrue(report.isOpen());
	}
	
	@Test
	public void testIsOpen() throws Exception {
		TradeReportImpl report =
			new TradeReportImpl(createTrade(descr1, time1, SELL, 50L, 0d, 0d));
		assertTrue(report.isOpen());
		report.addTrade(createTrade(descr1, time1, BUY, 25L, 0d, 0d));
		assertTrue(report.isOpen());
		report.addTrade(createTrade(descr1, time2, BUY, 25L, 0d, 0d));
		assertFalse(report.isOpen());
	}
	
	@Test
	public void testEquals() throws Exception {
		TradeReportImpl report = new TradeReportImpl(descr2, LONG, time1, time2,
				200L, 125L, 800.0d, 224.0d, 1024.0d, 650.0d);

		Variant<SecurityDescriptor> vDescr = new Variant<SecurityDescriptor>()
			.add(descr1)
			.add(descr2);
		Variant<PositionType> vType = new Variant<PositionType>(vDescr)
			.add(SHORT)
			.add(LONG);
		Variant<Date> vOpnTime = new Variant<Date>(vType)
			.add(format.parse("2013-01-01 00:00:00"))
			.add(format.parse("2015-12-31 00:00:00"));
		Variant<Date> vClsTime = new Variant<Date>(vOpnTime)
			.add(format.parse("2013-01-02 00:00:00"))
			.add(format.parse("1992-01-02 00:00:00"))
			.add(null);
		Variant<Long> vOpnQty = new Variant<Long>(vClsTime)
			.add(200L)
			.add(450L);
		Variant<Long> vClsQty = new Variant<Long>(vOpnQty)
			.add(125L)
			.add(345L)
			.add(null);
		Variant<Double> vOpnPrice = new Variant<Double>(vClsQty)
			.add(800.00d)
			.add(250.00d);
		Variant<Double> vClsPrice = new Variant<Double>(vOpnPrice)
			.add(224.0d)
			.add(36.00d)
			.add(null);
		Variant<Double> vOpnVol = new Variant<Double>(vClsPrice)
			.add(1024.0d)
			.add(26.00d);
		Variant<Double> vClsVol = new Variant<Double>(vOpnVol)
			.add(650.0d)
			.add(98.00d)
			.add(null);
		Variant<?> iterator = vClsVol;
		int foundCnt = 0;
		TradeReportImpl x = null, found = null;
		do {
			x = new TradeReportImpl(vDescr.get(), vType.get(),
					vOpnTime.get(), vClsTime.get(),
					vOpnQty.get(), vClsQty.get(),
					vOpnPrice.get(), vClsPrice.get(),
					vOpnVol.get(), vClsVol.get());
			if( report.equals(x) ) {
				foundCnt++;
				found = x;
			}
		} while( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(descr2, found.getSecurityDescriptor());
		assertSame(LONG, found.getType());
		assertEquals(format.parse("2013-01-01 00:00:00"), found.getEnterTime());
		assertEquals(format.parse("2013-01-02 00:00:00"), found.getExitTime());
		assertEquals(new Long(200L), found.getQty());
		assertEquals(new Long(75L), found.getUncoveredQty());
		assertEquals(4.00d, found.getEnterPrice(), 0.001d);
		assertEquals(1.792, found.getExitPrice(), 0.001d);
		assertEquals(1024.0, found.getEnterVolume(), 0.001d);
		assertEquals(650.0d, found.getExitVolume(), 0.001d);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		TradeReportImpl report = new TradeReportImpl(descr2, LONG, time1, time2,
				200L, 125L, 800.0d, 224.0d, 1024.0d, 650.0d);
		
		assertTrue(report.equals(report));
		assertFalse(report.equals(null));
		assertFalse(report.equals(this));
	}
	
	@Test
	public void testAddTrade() throws Exception {
		List<FR> fix = new Vector<FR>();
		// #0, Короткая, закрытая
		FR row = new FR(new TradeReportImpl(descr1, SHORT,
				format.parse("1998-08-01 20:35:00"),
				format.parse("1998-08-02 00:00:00"),
				200L, 200L,
				2000.0d, 1800.0d,
				4000.0d, 3600.0d), null);
		row.trades.add(createTrade(descr1, "1998-08-01 20:35:00",
				SELL, 100L, 10.0d, 2000.0d));
		row.trades.add(createTrade(descr1, "1998-08-01 22:48:15",
				SELL, 50L, 9.0d, 900.0d));
		row.trades.add(createTrade(descr1, "1998-08-01 23:15:00",
				SELL, 50L, 11.0d, 1100.0d));
		row.trades.add(createTrade(descr1, "1998-08-01 23:59:00",
				BUY, 100L, 8.0d, 1800.0d));
		row.trades.add(createTrade(descr1, "1998-08-02 00:00:00",
				BUY, 100L, 10.0d, 1800.0d));
		fix.add(row);
		
		// #1, Длинная, не закрытая
		row = new FR(new TradeReportImpl(descr2, LONG,
				format.parse("2001-01-01 00:00:00"),
				null,
				200L, null,
				2000.0d, null,
				4000.0d, null), null);
		row.trades.add(createTrade(descr2, "2001-01-01 00:00:00",
				BUY, 100L, 10.0d, 2000.0d));
		row.trades.add(createTrade(descr2, "2001-01-02 00:00:00",
				BUY, 100L, 10.0d, 2000.0d));
		fix.add(row);
		
		// #2, Длинная, частично-закрытая
		row = new FR(new TradeReportImpl(descr1, LONG,
				format.parse("1998-01-01 00:00:00"),
				null,
				200L, 50L,
				2000.0d, 500.0d,
				4000.0d, 1000.0d), null);
		row.trades.add(createTrade(descr1, "1998-01-01 00:00:00",
				BUY, 200L, 10.0d, 4000.0d));
		row.trades.add(createTrade(descr1, "1998-01-01 01:00:00",
				SELL, 50L, 10.0d, 1000.0d));
		fix.add(row);
		
		// #3, Длинная, закрытая, с разворотом одной сделкой
		row = new FR(new TradeReportImpl(descr1, LONG,
				format.parse("1996-06-01 00:00:00"),
				format.parse("1996-06-02 00:00:00"),
				1L, 1L,
				138770d, 138380d,
				86951.89d, 86726.32d),
			new TradeReportImpl(descr1, SHORT,
				format.parse("1996-06-02 00:00:00"),
				null,
				1L, null,
				138380d, null,
				86726.32d, null));
		row.trades.add(createTrade(descr1, "1996-06-01 00:00:00",
				BUY, 1L, 138770d, 86951.89d));
		row.trades.add(createTrade(descr1, "1996-06-02 00:00:00",
				SELL, 2L, 138380d, 173452.64d));
		fix.add(row);
		
		// #4, Короткая, закрытая, с сокращением и разворотом
		row = new FR(new TradeReportImpl(descr2, SHORT,
				format.parse("2018-01-15 03:00:01"),
				format.parse("2018-01-15 03:00:10"),
				10L, 10L,
				50d,  40d,
				100d, 80d),
			new TradeReportImpl(descr2, LONG,
				format.parse("2018-01-15 03:00:10"),
				null,
				5L, null,
				20d, null,
				40d, null));
		row.trades.add(createTrade(descr2, "2018-01-15 03:00:01",
				SELL, 4L, 5d, 40d));
		row.trades.add(createTrade(descr2, "2018-01-15 03:00:05",
				SELL, 6L, 5d, 60d));
		row.trades.add(createTrade(descr2, "2018-01-15 03:00:10",
				BUY, 5L, 4d, 40d));
		row.trades.add(createTrade(descr2, "2018-01-15 03:00:10",
				BUY, 10L, 4d, 80d));
		fix.add(row);
		
		for ( int i = 0; i < fix.size(); i ++ ) {
			String msg = "At #" + i;
			row = fix.get(i);
			TradeReportImpl report = new TradeReportImpl(row.trades.get(0));
			EditableTradeReport last = null;
			for ( int j = 1; j < row.trades.size(); j ++ ) {
				last = report.addTrade(row.trades.get(j));
			}
			assertEquals(msg, row.expected, report);
			if ( row.result == null ) {
				assertNull(msg, last);
			} else {
				assertEquals(msg, row.result, last);
			}
		}
	}

	@Test
	public void testCompareTo() throws Exception {
		TradeReportImpl report1 = new TradeReportImpl(descr2, SHORT, time1, null,
				1L, null, 0d, null, 0d, null),
			report2 = new TradeReportImpl(descr1, LONG, time2, null,
				1L, null, 0d, null, 0d, null),
			report3 = new TradeReportImpl(descr1, LONG, time2, null,
				1L, null, 0d, null, 0d, null);
		// сравнивается только время открытия
		assertEquals(1, report1.compareTo(null));
		assertEquals(0, report2.compareTo(report3));
		assertEquals(-1, report1.compareTo(report2));
		assertEquals(1, report2.compareTo(report1));
	}
	
	@Test
	public void testClone() throws Exception {
		TradeReportImpl report = new TradeReportImpl(descr2, LONG, time1, null,
				200L, null, 800.0d, null, 1024.0d, null);
		TradeReport copy = report.clone();
		assertEquals(report, copy);
		assertNotSame(report, copy);
		report.addTrade(createTrade(descr2, time2, SELL, 5L, 4d, 12d));
		assertFalse(report.equals(copy));
	}
	
	@Test
	public void testGetProfit() throws Exception {
		Object fix[][] = {
			// type, 			  entr.vol, exit vol,  profit, profit perc.
			{ PositionType.LONG,  83128.15d,      null,     null,	  null },
			{ PositionType.LONG,  83128.15d, 82942.05d, -186.10d, -0.2239d },
			{ PositionType.SHORT, 82284.60d,      null,	   null,	  null },
			{ PositionType.SHORT, 82284.60d, 82419.42d, -134.82d, -0.1638d },
			{ PositionType.SHORT, 82090.59d, 81922.95d,  167.64d,  0.2042d },
			{ PositionType.LONG,  82335.60d, 82942.05d,  606.45d,  0.7366d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			TradeReport report = new TradeReportImpl(descr1,
					(PositionType) fix[i][0], time1, null,
					1L, null, 0d, null, (Double) fix[i][1], (Double) fix[i][2]);
			if ( fix[i][3] == null ) {
				assertNull(msg, report.getProfit());
				assertNull(msg, report.getProfitPerc());
			} else {
				double p = (Double) fix[i][3];
				double pp = (Double) fix[i][4];
				assertEquals(msg, p, report.getProfit(), 0.01d);
				assertEquals(msg, pp, report.getProfitPerc(), 0.00001d);
			}
		}
	}

}