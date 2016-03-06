package ru.prolib.aquila.ui.plugin.getters;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.core.IsInstanceOf;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

/**
 * $Id$
 */
public class SecurityGettersTest {

	private static IMocksControl control;
	private Symbol symbol;
	private Object security;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		EditableTerminal terminal = new BasicTerminalBuilder().buildTerminal();
		
		symbol = new Symbol("GAZP", "EQBR", "RUR", SymbolType.STOCK);
		EditableSecurity sc = terminal.getEditableSecurity(symbol);
		Map<Integer, Object> tokens = new HashMap<Integer, Object>();
		tokens.put(SecurityField.SCALE, 3);
		tokens.put(SecurityField.LOWER_PRICE_LIMIT, 90.00d);
		tokens.put(SecurityField.UPPER_PRICE_LIMIT, 130.00d);
		tokens.put(SecurityField.LOT_SIZE, 1);
		tokens.put(SecurityField.TICK_VALUE, 290.34d);
		tokens.put(SecurityField.TICK_SIZE, 1.00d);
		tokens.put(SecurityField.DISPLAY_NAME, "zulu4");
		tokens.put(SecurityField.OPEN_PRICE, 13.45d);
		tokens.put(SecurityField.LOW_PRICE, 24.56d);
		tokens.put(SecurityField.HIGH_PRICE, 18.44d);
		tokens.put(SecurityField.CLOSE_PRICE, 98.15d);
		sc.update(tokens);
		sc.consume(new L1UpdateImpl(symbol, Tick.of(TickType.TRADE, 20.44d, 1L)));
		sc.consume(new L1UpdateImpl(symbol, Tick.of(TickType.ASK, 12.34d, 1000L)));
		sc.consume(new L1UpdateImpl(symbol, Tick.of(TickType.BID, 34.56d, 2000L)));
		security = (Object) sc;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSecurity() {
		GSecurity g = new GSecurity();
		IsInstanceOf.instanceOf(G.class).matches(g);		
		SecurityEvent e = new SecurityEvent(
				control.createMock(EventType.class), (Security) security);
		assertEquals(security, g.get(e));
	}
	
	@Test
	public void testType() throws Exception {
		GSecurityType g = new GSecurityType();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("Stock", g.get(security));
	}
	
	@Test
	public void testSymbol() throws Exception {
		GSecuritySymbol g = new GSecuritySymbol();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("GAZP", g.get(security));
	}
	
	@Test
	public void testStatus() {
		GSecurityStatus g = new GSecurityStatus();
		IsInstanceOf.instanceOf(G.class).matches(g);
		assertNull(g.get(security));
	}
	
	@Test
	public void testPrecision() throws Exception {
		GSecurityPrecision g = new GSecurityPrecision();
		IsInstanceOf.instanceOf(GInteger.class).matches(g);
		assertEquals((Integer) 3, g.get(security));	
	}
	
	@Test
	public void testOpenPrice() throws Exception {
		GSecurityOpenPrice g = new GSecurityOpenPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(13.45d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testName() throws Exception {
		GSecurityName g = new GSecurityName();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("zulu4", g.get(security));
	}
	
	@Test
	public void testMinStep() throws Exception {
		GSecurityMinStep g = new GSecurityMinStep();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(1.00d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testLowPrice() throws Exception {
		GSecurityLowPrice g = new GSecurityLowPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(24.56d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testLotSize() throws Exception {
		GSecurityLotSize g = new GSecurityLotSize();
		IsInstanceOf.instanceOf(GInteger.class).matches(g);
		assertEquals((Integer) 1, g.get(security));	
	}
	
	@Test
	public void testLastPrice() throws Exception {
		GSecurityLastPrice g = new GSecurityLastPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(20.44d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testHighPrice() throws Exception {
		GSecurityHighPrice g = new GSecurityHighPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(18.44d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testCurrency() throws Exception {
		GSecurityCurrency g = new GSecurityCurrency();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("RUR", g.get(security));
	}
	
	@Test
	public void testClosePrice() throws Exception {
		GSecurityClosePrice g = new GSecurityClosePrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(98.15d, g.get(security), 0.0001d);		
	}
	
	@Test
	public void testClass() throws Exception {
		GSecurityClass g = new GSecurityClass();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("EQBR", g.get(security));
	}
	
	@Test
	public void testBidSize() throws Exception {
		GSecurityBidSize g = new GSecurityBidSize();
		IsInstanceOf.instanceOf(GLong.class).matches(g);
		assertEquals((Long) 2000l, g.get(security));
	}
	
	@Test
	public void testBidPrice() throws Exception {
		GSecurityBidPrice g = new GSecurityBidPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(34.56d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testAskSize() throws Exception {
		GSecurityAskSize g = new GSecurityAskSize();
		IsInstanceOf.instanceOf(GLong.class).matches(g);
		assertEquals((Long) 1000l, g.get(security));
	}

	@Test
	public void testAskPrice() throws Exception {
		GSecurityAskPrice g = new GSecurityAskPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(12.34d, g.get(security), 0.0001d);		
	}
	
	@Test
	public void testMinPrice() throws Exception {
		GDouble g = new GSecurityMinPrice();
		assertEquals(90.0d, g.get(security), 0.01d);
	}
	
	@Test
	public void testMaxPrice() throws Exception {
		GDouble g = new GSecurityMaxPrice();
		assertEquals(130.0d, g.get(security), 0.01d);
	}
	
	@Test
	public void testMinStepPrice() throws Exception {
		GDouble g = new GSecurityMinStepPrice();
		assertEquals(290.34d, g.get(security), 0.01d);
	}
	
}
