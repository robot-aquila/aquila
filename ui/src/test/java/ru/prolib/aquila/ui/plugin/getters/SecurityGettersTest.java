package ru.prolib.aquila.ui.plugin.getters;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.hamcrest.core.IsInstanceOf;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
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
		sc.setPrecision(3);
		sc.setMinPrice(90.00d);
		sc.setMaxPrice(130.00d);
		sc.setLotSize(1);
		sc.setMinStepPrice(0.1d);
		sc.setMinStepSize(1.00d);
		sc.setLastPrice(20.44d);
		sc.setDisplayName("zulu4");
		sc.setAskPrice(12.34d);
		sc.setAskSize(1000l);
		sc.setBidPrice(34.56d);
		sc.setBidSize(2000l);
		sc.setOpenPrice(13.45d);
		sc.setClosePrice(98.15d);
		sc.setLowPrice(24.56d);
		sc.setHighPrice(18.44d);
		sc.setStatus(SecurityStatus.TRADING);
		sc.setMinStepPrice(290.34d);
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
		assertEquals(SecurityStatus.TRADING, g.get(security));
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
