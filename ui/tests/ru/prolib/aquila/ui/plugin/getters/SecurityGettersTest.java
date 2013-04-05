package ru.prolib.aquila.ui.plugin.getters;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.hamcrest.core.IsInstanceOf;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityImpl;
import ru.prolib.aquila.core.BusinessEntities.SecurityStatus;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.GDouble;
import ru.prolib.aquila.core.data.GInteger;
import ru.prolib.aquila.core.data.GLong;
import ru.prolib.aquila.core.data.GString;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class SecurityGettersTest {

	private static IMocksControl control;
	private SecurityDescriptor descr;
	private SecurityImpl security;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		Terminal terminal = control.createMock(Terminal.class);
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		
		descr = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
		security = new SecurityImpl(terminal, descr, dispatcher,
				control.createMock(EventType.class), control.createMock(EventType.class));
		security.setPrecision(3);
		security.setMinPrice(90.00d);
		security.setMaxPrice(130.00d);
		security.setLotSize(1);
		security.setMinStepPrice(0.1d);
		security.setMinStepSize(1.00d);
		security.setLastPrice(20.44d);
		security.setDisplayName("zulu4");
		security.setAskPrice(12.34d);
		security.setAskSize(1000l);
		security.setBidPrice(34.56d);
		security.setBidSize(2000l);
		security.setOpenPrice(13.45d);
		security.setClosePrice(98.15d);
		security.setLowPrice(24.56d);
		security.setHighPrice(18.44d);
		security.setStatus(SecurityStatus.TRADING);
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
				control.createMock(EventType.class), security);
		assertEquals(security, g.get(e));
	}
	
	@Test
	public void testType() {
		GSecurityType g = new GSecurityType();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("Stock", g.get(security));
	}
	
	@Test
	public void testSymbol() {
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
	public void testPrecision() {
		GSecurityPrecision g = new GSecurityPrecision();
		IsInstanceOf.instanceOf(GInteger.class).matches(g);
		assertEquals((Integer) 3, g.get(security));	
	}
	
	@Test
	public void testOpenPrice() {
		GSecurityOpenPrice g = new GSecurityOpenPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(13.45d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testName() {
		GSecurityName g = new GSecurityName();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("zulu4", g.get(security));
	}
	
	@Test
	public void testMinStep() {
		GSecurityMinStep g = new GSecurityMinStep();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(1.00d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testLowPrice() {
		GSecurityLowPrice g = new GSecurityLowPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(24.56d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testLotSize() {
		GSecurityLotSize g = new GSecurityLotSize();
		IsInstanceOf.instanceOf(GInteger.class).matches(g);
		assertEquals((Integer) 1, g.get(security));	
	}
	
	@Test
	public void testLastPrice() {
		GSecurityLastPrice g = new GSecurityLastPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(20.44d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testHighPrice() {
		GSecurityHighPrice g = new GSecurityHighPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(18.44d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testCurrency() {
		GSecurityCurrency g = new GSecurityCurrency();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("RUR", g.get(security));
	}
	
	@Test
	public void testClosePrice() {
		GSecurityClosePrice g = new GSecurityClosePrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(98.15d, g.get(security), 0.0001d);		
	}
	
	@Test
	public void testClass() {
		GSecurityClass g = new GSecurityClass();
		IsInstanceOf.instanceOf(GString.class).matches(g);
		assertEquals("EQBR", g.get(security));
	}
	
	@Test
	public void testBidSize() {
		GSecurityBidSize g = new GSecurityBidSize();
		IsInstanceOf.instanceOf(GLong.class).matches(g);
		assertEquals((Long) 2000l, g.get(security));
	}
	
	@Test
	public void testBidPrice() {
		GSecurityBidPrice g = new GSecurityBidPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(34.56d, g.get(security), 0.0001d);	
	}
	
	@Test
	public void testAskSize() {
		GSecurityAskSize g = new GSecurityAskSize();
		IsInstanceOf.instanceOf(GLong.class).matches(g);
		assertEquals((Long) 1000l, g.get(security));
	}

	@Test
	public void testAskPrice() {
		GSecurityAskPrice g = new GSecurityAskPrice();
		IsInstanceOf.instanceOf(GDouble.class).matches(g);
		assertEquals(12.34d, g.get(security), 0.0001d);		
	}

}
