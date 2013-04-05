package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.BusinessEntities.utils.TradeFactoryImpl;

/**
 * 2012-11-07<br>
 * $Id: TradeFactoryImplTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeFactoryImplTest {
	private static IMocksControl control;
	private static Terminal terminal;
	private static TradeFactoryImpl factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		factory = new TradeFactoryImpl(terminal);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testCreateTrade() throws Exception {
		assertEquals(new Trade(terminal), factory.createTrade());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Terminal terminal2 = control.createMock(Terminal.class);
		assertTrue(factory.equals(new TradeFactoryImpl(terminal)));
		assertFalse(factory.equals(new TradeFactoryImpl(terminal2)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121109, 1125541)
			.append(terminal)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}

}
