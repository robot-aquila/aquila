package ru.prolib.aquila.core.report;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.report.trades.*;

public class ReportBuilderTest {
	private EditableTerminal terminal;
	private ReportBuilder builder;

	@Before
	public void setUp() throws Exception {
		terminal = new TerminalBuilder().createTerminal("foo");
		builder = new ReportBuilder();
	}
	
	@Test
	public void testCreateTradeReport() throws Exception {
		EventDispatcher d = terminal.getEventSystem()
		.createEventDispatcher("Trades");
		TradeReport expected = new CommonTradeReport(d, d.createType("Enter"),
				d.createType("Exit"), d.createType("Changed"));
		
		assertEquals(expected,
				builder.createTradeReport(terminal.getEventSystem()));
	}
	
	@Test
	public void testCreateTerminalTradeReport() throws Exception {
		TradeReport expected = new TerminalTradeReport(terminal,
				new StubTradeSelector(),
				builder.createTradeReport(terminal.getEventSystem()));
		
		assertEquals(expected, builder.createTerminalTradeReport(terminal));
	}
	
	@Test
	public void testCreateAccountTradeReport() throws Exception {
		TradeReport expected = new TerminalTradeReport(terminal,
				new AccountTradeSelector(new Account("TEST")),
				builder.createTradeReport(terminal.getEventSystem()));
		
		assertEquals(expected,
			builder.createAccountTradeReport(terminal, new Account("TEST")));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
		assertTrue(builder.equals(new ReportBuilder()));
	}

}
