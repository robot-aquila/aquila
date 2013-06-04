package ru.prolib.aquila.core.report;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

public class ReportBuilderTest {
	private EditableTerminal terminal;
	private ReportBuilder builder;

	@Before
	public void setUp() throws Exception {
		terminal = new TerminalBuilder().createTerminal("foo");
		builder = new ReportBuilder();
	}
	
	@Test
	public void testCreatePortfolioTrades() throws Exception {
		Portfolio portfolio = terminal.createPortfolio(new Account("TEST"));
		EventDispatcher d = terminal.getEventSystem()
			.createEventDispatcher("Trades");
		Trades expected = new PortfolioTrades(new TradesImpl(d,
				d.createType("Enter"), d.createType("Exit"),
				d.createType("Changed")), portfolio);
		
		assertEquals(expected, builder.createPortfolioTrades(portfolio));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
		assertTrue(builder.equals(new ReportBuilder()));
	}

}
