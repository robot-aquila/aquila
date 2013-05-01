package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
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
		trade.setDirection(OrderDirection.BUY);
		trade.setTime(cal.getTime());
		trade.setPrice(100.00d);
		trade.setQty(1L);
		trade.setVolume(200.00d);
		trade.setOrderId(1024L);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals((Long) 105L, trade.getId());
		assertSame(terminal, trade.getTerminal());
		assertEquals(descr, trade.getSecurityDescriptor());
		assertSame(OrderDirection.BUY, trade.getDirection());
		assertEquals(cal.getTime(), trade.getTime());
		assertEquals(100.00d, trade.getPrice(), 0.001d);
		assertEquals((Long) 1L, trade.getQty());
		assertEquals(200.00d, trade.getVolume(), 0.001d);
		assertEquals((Long) 1024L, trade.getOrderId());
	}
	
	@Test
	public void testToString() {
		control.replay();
		assertEquals("Trade: " +
			"2010-09-01 03:45:15 #105 Buy SBER@EQBR(STK/RUB) 1x100.0 Vol=200.0",
			trade.toString());
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
		Variant<OrderDirection> vDir = new Variant<OrderDirection>(vSecDescr)
			.add(null)
			.add(OrderDirection.BUY)
			.add(OrderDirection.SELL);
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
			actual.setTime(vTime.get());
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
		assertSame(OrderDirection.BUY, found.getDirection());
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
			.append(OrderDirection.BUY)
			.append(cal.getTime())
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

}
