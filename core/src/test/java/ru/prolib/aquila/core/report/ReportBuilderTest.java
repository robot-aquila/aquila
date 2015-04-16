package ru.prolib.aquila.core.report;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.report.trades.*;

public class ReportBuilderTest {
	private EventSystem es;
	private EditableTerminal terminal;
	private ReportBuilder builder;

	@Before
	public void setUp() throws Exception {
		terminal = new TerminalBuilder().buildTerminal();
		es = terminal.getEventSystem();
		es.getEventQueue().start();
		builder = new ReportBuilder();
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testCreateReport_EventSystem() throws Exception {
		TradeReport expected = new CommonTR(es, new CommonTREventDispatcher(es));
		
		TradeReport actual = builder.createReport(terminal.getEventSystem());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReport_Terminal() throws Exception {
		TradeReport expected = new TerminalTradeReport(terminal,
				new StubTradeSelector(),
				builder.createReport(terminal.getEventSystem()));
		
		TradeReport actual = builder.createReport(terminal);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateReport_Account() throws Exception {
		TradeReport expected = new TerminalTradeReport(terminal,
				new AccountTradeSelector(new Account("TEST")),
				builder.createReport(terminal.getEventSystem()));
		
		TradeReport actual = builder.createReport(terminal, new Account("TEST")); 
		assertEquals(expected, actual);
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
		
		TradeReport actual = builder.createReport(orders);
		assertEquals(expected, actual);
	}

}
