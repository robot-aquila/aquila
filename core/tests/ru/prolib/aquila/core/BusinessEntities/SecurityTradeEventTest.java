package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.BMUtils;
import ru.prolib.aquila.core.data.Tick;

/**
 * 2012-06-01<br>
 * $Id: SecurityTradeEventTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class SecurityTradeEventTest {
	private static final SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("alpha", "phi", "USD");
	}
	
	private IMocksControl control;
	private Security security;
	private EventType eventType;
	private EditableTerminal terminal;
	private Trade trade;
	private SecurityTradeEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = new TerminalImpl("zulu24");
		security = terminal.getEditableSecurity(descr);
		eventType = security.OnTrade();
		trade = new BMUtils().tradeFromTick(
			new Tick(new DateTime(2013, 11, 20, 0, 54, 39, 1), 125d, 10d),
			security);
		event = new SecurityTradeEvent(eventType, security, trade);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType, event.getType());
		assertSame(security, event.getSecurity());
		assertSame(trade, event.getTrade());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		BMUtils utils = new BMUtils();
		Trade trade1 = utils.tradeFromTick(
			new Tick(new DateTime(2013, 11, 20, 0, 54, 39, 1), 125d, 10d),
			security);
		Trade trade2 = utils.tradeFromTick(
			new Tick(new DateTime(2014, 11, 20, 1,  4,  2, 9), 132d, 15d),
			security);
		
		
		
		fail("TODO: ");
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("SecurityTradeEvent[" + trade.toString() + "]", event.toString());
	}

}
