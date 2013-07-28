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
	public void testCreateReport_EventSystem() throws Exception {
		EventDispatcher d = terminal.getEventSystem()
		.createEventDispatcher("Trades");
		TradeReport expected = new CommonTradeReport(d, d.createType("Enter"),
				d.createType("Exit"), d.createType("Changed"));
		
		assertEquals(expected,
				builder.createReport(terminal.getEventSystem()));
	}
	
	@Test
	public void testCreateReport_Terminal() throws Exception {
		TradeReport expected = new TerminalTradeReport(terminal,
				new StubTradeSelector(),
				builder.createReport(terminal.getEventSystem()));
		
		assertEquals(expected, builder.createReport(terminal));
	}
	
	@Test
	public void testCreateReport_Account() throws Exception {
		TradeReport expected = new TerminalTradeReport(terminal,
				new AccountTradeSelector(new Account("TEST")),
				builder.createReport(terminal.getEventSystem()));
		
		assertEquals(expected,
			builder.createReport(terminal, new Account("TEST")));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
		assertTrue(builder.equals(new ReportBuilder()));
	}
	
	@Test
	public void testCreateReport_OrderPool() throws Exception {
		OrderPool orders = new OrderPoolImpl(terminal);
		TradeReport expected = new TerminalTradeReport(terminal,
				new OrderPoolTradeSelector(orders),
				builder.createReport(terminal.getEventSystem()));
		
		assertEquals(expected, builder.createReport(orders));
	}

}
