package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.time.Instant;
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
	private static Terminal terminal, terminal2;
	private static Symbol symbol;
	private Trade trade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol = new Symbol("SBER", "EQBR", "RUB", SymbolType.STOCK);
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		terminal2 = control.createMock(Terminal.class);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		trade = new Trade(terminal);
		trade.setId(105L);
		trade.setSymbol(symbol);
		trade.setDirection(OrderAction.BUY);
		trade.setTime(Instant.parse("2010-08-01T03:45:15Z"));
		trade.setPrice(100.00d);
		trade.setQty(1L);
		trade.setVolume(200.00d);
		trade.setOrderId(1024L);
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param id номер сделки
	 * @param time время в формате yyyy-MM-ddTHH:mm:ss
	 * @return сделка
	 * @throws Exception
	 */
	private Trade createTrade(Long id, String time) throws Exception {
		Trade trade = new Trade(terminal);
		trade.setId(id);
		trade.setTime(Instant.parse(time));
		return trade;
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertEquals((Long) 105L, trade.getId());
		assertSame(terminal, trade.getTerminal());
		assertEquals(symbol, trade.getSymbol());
		assertSame(OrderAction.BUY, trade.getDirection());
		assertEquals(Instant.parse("2010-08-01T03:45:15Z"), trade.getTime());
		assertEquals(100.00d, trade.getPrice(), 0.001d);
		assertEquals(1L, trade.getQty());
		assertEquals(200.00d, trade.getVolume(), 0.001d);
		assertEquals((Long) 1024L, trade.getOrderId());
	}
	
	@Test
	public void testConstruct0() throws Exception {
		trade = new Trade();
		assertNull(trade.getTerminal());
	}
	
	@Test
	public void testSetTerminal() throws Exception {
		assertSame(terminal, trade.getTerminal());
		trade.setTerminal(terminal2);
		assertSame(terminal2, trade.getTerminal());
	}
	
	@Test
	public void testToString() {
		control.replay();
		assertEquals("Trade: " + trade.getTime()  + " #105 BUY " +
				"S:SBER@EQBR:RUB 1x100.0 Vol=200.0", trade.toString());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Long> vId = new Variant<Long>()
			.add(null)
			.add(100500L)
			.add(105L);
		Variant<Symbol> vSymbol = new Variant<Symbol>(vId)
			.add(null)
			.add(symbol)
			.add(control.createMock(Symbol.class));
		Variant<OrderAction> vDir = new Variant<OrderAction>(vSymbol)
			.add(null)
			.add(OrderAction.BUY)
			.add(OrderAction.SELL);
		Variant<Instant> vTime = new Variant<Instant>(vDir)
			.add(null)
			.add(Instant.parse("2010-08-01T03:45:15Z"))
			.add(Instant.now());
		Variant<Double> vPrice = new Variant<Double>(vTime)
			.add(100.00d)
			.add(123.45d);
		Variant<Long> vQty = new Variant<Long>(vPrice)
			.add(1L)
			.add(2L);
		Variant<Double> vVol = new Variant<Double>(vQty)
			.add(200.00d)
			.add(400.00d);
		Variant<Long> vOrdId = new Variant<Long>(vVol)
			.add(null)
			.add(1024L)
			.add(256L);
		Variant<Terminal> vTerm = new Variant<Terminal>(vOrdId)
			.add(null)
			.add(terminal)
			.add(terminal2);
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		Trade found = null;
		do {
			Trade actual = new Trade(vTerm.get());
			actual.setId(vId.get());
			actual.setSymbol(vSymbol.get());
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
		assertSame(symbol, found.getSymbol());
		assertSame(OrderAction.BUY, found.getDirection());
		assertEquals(trade.getTime(), found.getTime());
		assertEquals(100.00d, found.getPrice(), 0.001d);
		assertEquals(1L, found.getQty());
		assertEquals(200.00d, found.getVolume(), 0.001d);
		assertEquals((Long) 1024L, found.getOrderId());
		assertSame(terminal, found.getTerminal());
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
			.append(symbol)
			.append(OrderAction.BUY)
			.append(Instant.parse("2010-08-01T03:45:15Z"))
			.append(100.00d)
			.append(1L)
			.append(200.00d)
			.append(1024L)
			.append(terminal)
			.toHashCode();
		assertEquals(hashCode, trade.hashCode());
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		Security security = control.createMock(Security.class);
		expect(terminal.getSecurity(eq(symbol))).andReturn(security);
		control.replay();
		assertSame(security, trade.getSecurity());
		control.verify();
	}
	
	@Test
	public void testCompareTo() throws Exception {
		Trade t0 = createTrade(100L, "2013-05-01T20:00:00Z");
		assertEquals( 1,t0.compareTo(null));
		assertEquals( 0,t0.compareTo(createTrade(100L, "2013-05-01T20:00:00Z")));
		assertEquals( 1,t0.compareTo(createTrade( 99L, "2013-05-01T20:00:00Z")));
		assertEquals(-1,t0.compareTo(createTrade(101L, "2013-05-01T20:00:00Z")));
		assertEquals(-1,t0.compareTo(createTrade(100L, "2013-05-01T20:00:01Z")));
		assertEquals( 1,t0.compareTo(createTrade(100L, "2013-05-01T19:00:59Z")));
	}

}
