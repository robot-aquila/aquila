package ru.prolib.aquila.ChaosTheory;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

public class RiskManagerNoneTest {
	private RiskManagerNone rm;
	
	@BeforeClass
	public static final void setUpBefreClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		rm = new RiskManagerNone(2);
	}
	
	@Test
	public void testAccessors() {
		assertEquals(2, rm.getLongSize(12.34d));
		assertEquals(2, rm.getShortSize(56.78d));
	}

}
