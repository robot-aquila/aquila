package ru.prolib.aquila.ChaosTheory;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class RiskManagerSimpleLimitTest {
	IMocksControl control;
	PortfolioState state;
	RiskManagerSimpleLimit rm;
	
	@BeforeClass
	public static final void setUpBefreClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		state = control.createMock(PortfolioState.class);
		rm = new RiskManagerSimpleLimit(state, 5);
	}
	
	@Test
	public void testAccessors() throws Exception {
		expect(state.getPosition()).andReturn(4);
		expect(state.getPosition()).andReturn(5);
		expect(state.getPosition()).andReturn(6);

		expect(state.getPosition()).andReturn(-4);
		expect(state.getPosition()).andReturn(-5);
		expect(state.getPosition()).andReturn(-6);
		control.replay();
		
		assertEquals(1, rm.getLongSize(111.22d));
		assertEquals(0, rm.getLongSize(111.22d));
		assertEquals(0, rm.getLongSize(111.22d));
		
		assertEquals(1, rm.getShortSize(222.11d));
		assertEquals(0, rm.getShortSize(222.11d));
		assertEquals(0, rm.getShortSize(222.11d));
		
		control.verify();
	}

}
