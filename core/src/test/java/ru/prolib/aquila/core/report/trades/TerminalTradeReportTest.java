package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.report.*;
import ru.prolib.aquila.core.utils.Variant;

public class TerminalTradeReportTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EventTypeSI onEvent;
	private Terminal terminal;
	private EditableTradeReport underlying;
	private Order order;
	private Trade trade;
	private TradeSelector selector;
	private RTrade record;
	private Security security;
	private TerminalTradeReport report;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RI", "SPFB", "USD", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		record = control.createMock(RTrade.class);
		security = control.createMock(Security.class);
		onEvent = control.createMock(EventTypeSI.class);
		terminal = control.createMock(Terminal.class);
		underlying = control.createMock(EditableTradeReport.class);
		order = control.createMock(Order.class);
		trade = control.createMock(Trade.class);
		selector = control.createMock(TradeSelector.class);
		report = new TerminalTradeReport(terminal, selector, underlying);
		
		expect(terminal.OnOrderTrade()).andStubReturn(onEvent);
	}
	
	@Test
	public void testStart() throws Exception {
		underlying.start();
		onEvent.addListener(report);
		control.replay();
		
		report.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		onEvent.removeListener(report);
		underlying.stop();
		control.replay();
		
		report.stop();
		
		control.verify();
	}
	
	@Test
	public void testSize() throws Exception {
		expect(underlying.size()).andReturn(819);
		control.replay();
		
		assertEquals(819, report.size());
		
		control.verify();
	}
	
	@Test
	public void testGetRecords() throws Exception {
		List<RTrade> list = new Vector<RTrade>();
		expect(underlying.getRecords()).andReturn(list);
		control.replay();
		
		assertSame(list, report.getRecords());
		
		control.verify();
	}
	
	@Test
	public void testGetRecord() throws Exception {
		RTrade tr = control.createMock(RTrade.class);
		expect(underlying.getRecord(18)).andReturn(tr);
		control.replay();
		
		assertSame(tr, report.getRecord(18));
		
		control.verify();
	}
	
	@Test
	public void testOnEnter() throws Exception {
		expect(underlying.OnEnter()).andReturn(onEvent);
		control.replay();
		
		assertSame(onEvent, report.OnEnter());
		
		control.verify();
	}
	
	@Test
	public void testOnExit() throws Exception {
		expect(underlying.OnExit()).andReturn(onEvent);
		control.replay();
		
		assertSame(onEvent, report.OnExit());
		
		control.verify();
	}
	
	@Test
	public void testOnChanged() throws Exception {
		expect(underlying.OnChanged()).andReturn(onEvent);
		control.replay();
		
		assertSame(onEvent, report.OnChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_TradeApproved() throws Exception {
		OrderTradeEvent e = new OrderTradeEvent((EventTypeSI) onEvent, order, trade);
		expect(selector.mustBeAdded(same(trade), same(order))).andReturn(true);
		underlying.addTrade(same(trade));
		control.replay();
		
		report.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_TradeRejected() throws Exception {
		OrderTradeEvent e = new OrderTradeEvent((EventTypeSI) onEvent, order, trade);
		expect(selector.mustBeAdded(same(trade), same(order))).andReturn(false);
		control.replay();
		
		report.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(report.equals(report));
		assertFalse(report.equals(null));
		assertFalse(report.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Terminal t1 = new BasicTerminalBuilder().buildTerminal();
		Terminal t2 = new BasicTerminalBuilder().buildTerminal();
		report = new TerminalTradeReport(t1, selector, underlying);
		Variant<Terminal> vTerm = new Variant<Terminal>()
			.add(t1)
			.add(t2);
		Variant<TradeSelector> vSel = new Variant<TradeSelector>(vTerm)
			.add(selector)
			.add(control.createMock(TradeSelector.class));
		Variant<EditableTradeReport> vUnd =
				new Variant<EditableTradeReport>(vSel)
			.add(underlying)
			.add(control.createMock(EditableTradeReport.class));
		Variant<?> iterator = vUnd;
		int foundCnt = 0;
		TerminalTradeReport x, found = null;
		do {
			x = new TerminalTradeReport(vTerm.get(), vSel.get(), vUnd.get());
			if ( report.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(selector, found.getTradeSelector());
		assertSame(underlying, found.getUnderlyingReport());
	}
	
	@Test
	public void testGetCurrent_SD() throws Exception {
		expect(underlying.getCurrent(descr)).andReturn(record);
		control.replay();
		
		assertSame(record, report.getCurrent(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetCurrent_S() throws Exception {
		expect(underlying.getCurrent(security)).andReturn(record);
		control.replay();
		
		assertSame(record, report.getCurrent(security));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_SD() throws Exception {
		expect(underlying.getPosition(descr)).andReturn(5L);
		control.replay();
		
		assertEquals(5L, report.getPosition(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_S() throws Exception {
		expect(underlying.getPosition(security)).andReturn(-1L);
		control.replay();
		
		assertEquals(-1L, report.getPosition(security));
		
		control.verify();
	}

}
