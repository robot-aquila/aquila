package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;

/**
 * 2012-06-01<br>
 * $Id: SecurityTradeEventTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class SecurityTradeEventTest {
	private IMocksControl control;
	private EventType eventType;
	private Security security;
	private Terminal terminal;
	private Trade trade;
	private SecurityTradeEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType = control.createMock(EventType.class);
		security = control.createMock(Security.class);
		terminal = control.createMock(Terminal.class);
		trade = new Trade(terminal);
		event = new SecurityTradeEvent(eventType, security, trade);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType, event.getType());
		assertSame(security, event.getSecurity());
		assertSame(trade, event.getTrade());
	}

}
