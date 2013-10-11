package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-06-01<br>
 * $Id: TradeTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class TradeTest {
	private static IMocksControl control;
	private static Terminal terminal;
	private static SecurityDescriptor descr;
	private Trade trade;
	private static Calendar cal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		cal = Calendar.getInstance();
		cal.set(2010, 8, 1, 3, 45, 15); // 2010-08-01 03:45:15
		cal.set(Calendar.MILLISECOND, 0);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		trade = new Trade(terminal);
		trade.setId(105L);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(Direction.BUY);
		trade.setTime(new DateTime(cal.getTime()));
		trade.setPrice(100.00d);
		trade.setQty(1L);
		trade.setVolume(200.00d);
		trade.setOrderId(1024L);
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param id номер сделки
	 * @param time время в формате yyyy-MM-dd HH:mm:ss
	 * @return сделка
	 * @throws Exception
	 */
	private Trade createTrade(Long id, String time) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Trade trade = new Trade(terminal);
		trade.setId(id);
		trade.setTime(new DateTime(format.parse(time)));
		return trade;
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals((Long) 105L, trade.getId());
		assertSame(terminal, trade.getTerminal());
		assertEquals(descr, trade.getSecurityDescriptor());
		assertSame(Direction.BUY, trade.getDirection());
		assertEquals(new DateTime(2010, 9, 1, 3, 45, 15), trade.getTime());
		assertEquals(100.00d, trade.getPrice(), 0.001d);
		assertEquals((Long) 1L, trade.getQty());
		assertEquals(200.00d, trade.getVolume(), 0.001d);
		assertEquals((Long) 1024L, trade.getOrderId());
	}
	
	@Test
	public void testToString() {
		control.replay();
		assertEquals("Trade: 2010-09-01T03:45:15.000+04:00 #105 Buy " +
				"SBER@EQBR(STK/RUB) 1x100.0 Vol=200.0", trade.toString());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Long> vId = new Variant<Long>()
			.add(null)
			.add(100500L)
			.add(105L);
		Variant<SecurityDescriptor> vSecDescr =
				new Variant<SecurityDescriptor>(vId)
			.add(null)
			.add(descr)
			.add(control.createMock(SecurityDescriptor.class));
		Variant<Direction> vDir = new Variant<Direction>(vSecDescr)
			.add(null)
			.add(Direction.BUY)
			.add(Direction.SELL);
		Variant<Date> vTime = new Variant<Date>(vDir)
			.add(null)
			.add(cal.getTime())
			.add(new Date());
		Variant<Double> vPrice = new Variant<Double>(vTime)
			.add(null)
			.add(100.00d)
			.add(123.45d);
		Variant<Long> vQty = new Variant<Long>(vPrice)
			.add(null)
			.add(1L)
			.add(2L);
		Variant<Double> vVol = new Variant<Double>(vQty)
			.add(null)
			.add(200.00d)
			.add(400.00d);
		Variant<Long> vOrdId = new Variant<Long>(vVol)
			.add(null)
			.add(1024L)
			.add(256L);
		Variant<?> iterator = vOrdId;
		int foundCnt = 0;
		Trade found = null;
		do {
			Trade actual = new Trade(terminal);
			actual.setId(vId.get());
			actual.setSecurityDescriptor(vSecDescr.get());
			actual.setDirection(vDir.get());
			actual.setTime(new DateTime(vTime.get()));
			actual.setPrice(vPrice.get());
			actual.setQty(vQty.get());
			actual.setVolume(vVol.get());
			actual.setOrderId(vOrdId.get());
			if ( trade.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
			
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals((Long) 105L, found.getId());
		assertSame(descr, found.getSecurityDescriptor());
		assertSame(Direction.BUY, found.getDirection());
		assertEquals(trade.getTime(), found.getTime());
		assertEquals(100.00d, found.getPrice(), 0.001d);
		assertEquals((Long) 1L, found.getQty());
		assertEquals(200.00d, found.getVolume(), 0.001d);
		assertEquals((Long) 1024L, found.getOrderId());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(trade.equals(trade));
		assertFalse(trade.equals(null));
		assertFalse(trade.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, 120517)
			.append(105L)
			.append(descr)
			.append(Direction.BUY)
			.append(new DateTime(2010, 9, 1, 3, 45, 15))
			.append(100.00d)
			.append(1L)
			.append(200.00d)
			.append(1024L)
			.toHashCode();
		assertEquals(hashCode, trade.hashCode());
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		Security security = control.createMock(Security.class);
		expect(terminal.getSecurity(eq(descr))).andReturn(security);
		control.replay();
		assertSame(security, trade.getSecurity());
		control.verify();
	}
	
	@Test
	public void testCompareTo() throws Exception {
		Trade t0 = createTrade(100L, "2013-05-01 20:00:00");
		assertEquals( 1,t0.compareTo(null));
		assertEquals( 0,t0.compareTo(createTrade(100L, "2013-05-01 20:00:00")));
		assertEquals( 1,t0.compareTo(createTrade( 99L, "2013-05-01 20:00:00")));
		assertEquals(-1,t0.compareTo(createTrade(101L, "2013-05-01 20:00:00")));
		assertEquals(-1,t0.compareTo(createTrade(100L, "2013-05-01 20:00:01")));
		assertEquals( 1,t0.compareTo(createTrade(100L, "2013-05-01 19:00:59")));
	}

}
