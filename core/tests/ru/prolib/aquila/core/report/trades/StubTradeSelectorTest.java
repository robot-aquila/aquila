package ru.prolib.aquila.core.report.trades;

import static org.junit.Assert.*;

import org.junit.*;

public class StubTradeSelectorTest {
	private StubTradeSelector selector;

	@Before
	public void setUp() throws Exception {
		selector = new StubTradeSelector();
	}
	
	@Test
	public void testMustBeAdded() throws Exception {
		assertTrue(selector.mustBeAdded(null, null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(selector.equals(selector));
		assertFalse(selector.equals(null));
		assertFalse(selector.equals(this));
		assertTrue(selector.equals(new StubTradeSelector()));
	}

}
