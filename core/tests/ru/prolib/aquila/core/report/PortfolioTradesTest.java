package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.Variant;

public class PortfolioTradesTest {
	private static SecurityDescriptor descr1, descr2, descr3;
	private IMocksControl control;
	private EditableTrades subTrades;
	private EditablePortfolio portfolio;
	private EditableTerminal terminal;
	private EventType type;
	private PortfolioTradesHelper helper;
	private PortfolioTrades trades;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBER", "EQBR", "X", SecurityType.STK);
		descr2 = new SecurityDescriptor("GAZP", "EQBR", "Y", SecurityType.STK);
		descr3 = new SecurityDescriptor("MRSK", "EQBR", "Z", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		subTrades = control.createMock(EditableTrades.class);
		terminal = new TerminalBuilder().createTerminal("foo");
		portfolio = terminal.createPortfolio(new Account("TEST"));
		terminal.createSecurity(descr1);
		terminal.createSecurity(descr2);
		terminal.createSecurity(descr3);
		type = control.createMock(EventType.class);
		helper = control.createMock(PortfolioTradesHelper.class);
		trades = new PortfolioTrades(subTrades, portfolio, helper);
	}
	
	@Test
	public void testStart() throws Exception {
		EditablePosition p1, p2, p3;
		p1 = portfolio.getEditablePosition(terminal.getSecurity(descr1));
		p1.setCurrQty(1);
		p2 = portfolio.getEditablePosition(terminal.getSecurity(descr2));
		p2.setCurrQty(0);
		p3 = portfolio.getEditablePosition(terminal.getSecurity(descr3));
		p3.setCurrQty(-10);
		
		Trade t1 = control.createMock(Trade.class),
			t3 = control.createMock(Trade.class);
		
		subTrades.start();
		expect(helper.createInitialTrade(same(p1))).andReturn(t1);
		subTrades.addTrade(same(t1));
		expect(helper.createInitialTrade(same(p2))).andReturn(null);
		expect(helper.createInitialTrade(same(p3))).andReturn(t3);
		subTrades.addTrade(same(t3));
		control.replay();
		
		trades.start();
		
		control.verify();
		assertTrue(terminal.OnOrderTrade().isListener(trades));
	}
	
	@Test
	public void testStop() throws Exception {
		subTrades.start();
		subTrades.stop();
		control.replay();
		
		trades.start();
		trades.stop();
		
		control.verify();
		assertFalse(terminal.OnOrderTrade().isListener(trades));
	}
	
	@Test
	public void testGetTradeReportCount() throws Exception {
		expect(subTrades.getTradeReportCount()).andReturn(80);
		control.replay();
		
		assertEquals(80, trades.getTradeReportCount());
		
		control.verify();
	}
	
	@Test
	public void testGetTradeReports() throws Exception {
		List<TradeReport> list = new Vector<TradeReport>();
		expect(subTrades.getTradeReports()).andReturn(list);
		control.replay();
		
		assertSame(list, trades.getTradeReports());
		
		control.verify();
	}
	
	@Test
	public void testGetTradeReport() throws Exception {
		TradeReport report = control.createMock(TradeReport.class);
		expect(subTrades.getTradeReport(eq(18))).andReturn(report);
		control.replay();
		
		assertSame(report, trades.getTradeReport(18));
		
		control.verify();
	}
	
	@Test
	public void testOnEnter() throws Exception {
		expect(subTrades.OnEnter()).andReturn(type);
		control.replay();
		
		assertSame(type, trades.OnEnter());
		
		control.verify();
	}
	
	@Test
	public void testOnExit() throws Exception {
		expect(subTrades.OnExit()).andReturn(type);
		control.replay();
		
		assertSame(type, trades.OnExit());
		
		control.verify();
	}
	
	@Test
	public void testOnChanged() throws Exception {
		expect(subTrades.OnChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, trades.OnChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfNotOrderTrade() throws Exception {
		control.replay();
		
		trades.onEvent(new EventImpl(type));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfDifferentAccount() throws Exception {
		Trade t = control.createMock(Trade.class);
		Order o = control.createMock(Order.class);
		expect(o.getAccount()).andReturn(new Account("BAR"));
		control.replay();
		
		trades.onEvent(new OrderTradeEvent(terminal.OnOrderTrade(), o, t));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent() throws Exception {
		Trade t = control.createMock(Trade.class);
		Order o = control.createMock(Order.class);
		expect(o.getAccount()).andReturn(new Account("TEST"));
		subTrades.addTrade(same(t));
		control.replay();
		
		trades.onEvent(new OrderTradeEvent(terminal.OnOrderTrade(), o, t));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(trades.equals(trades));
		assertFalse(trades.equals(null));
		assertFalse(trades.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTrades> vTrds = new Variant<EditableTrades>()
			.add(subTrades)
			.add(control.createMock(EditableTrades.class));
		Variant<Portfolio> vPort = new Variant<Portfolio>(vTrds)
			.add(portfolio)
			.add(control.createMock(Portfolio.class));
		Variant<PortfolioTradesHelper> vHelp =
				new Variant<PortfolioTradesHelper>(vPort)
			.add(helper)
			.add(control.createMock(PortfolioTradesHelper.class));
		Variant<?> iterator = vHelp;
		int foundCnt = 0;
		PortfolioTrades x = null, found = null;
		do {
			x = new PortfolioTrades(vTrds.get(), vPort.get(), vHelp.get());
			if ( trades.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(subTrades, found.getTrades());
		assertSame(portfolio, found.getPortfolio());
		assertSame(helper, found.getHelper());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		Trades expected = new PortfolioTrades(subTrades, portfolio,
				new PortfolioTradesHelper());
		assertEquals(expected, new PortfolioTrades(subTrades, portfolio));
	}

}
