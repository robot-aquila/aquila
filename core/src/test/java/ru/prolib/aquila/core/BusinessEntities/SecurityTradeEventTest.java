package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BMUtils;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.data.Tick;

/**
 * 2012-06-01<br>
 * $Id: SecurityTradeEventTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class SecurityTradeEventTest {
	private static final Symbol symbol;
	
	static {
		symbol = new Symbol("alpha", "phi", "USD");
	}
	
	private IMocksControl control;
	private EditableSecurity security, security2;
	private EventTypeSI eventType, eventType2;
	private EditableTerminal terminal;
	private Trade trade;
	private SecurityTradeEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = new BasicTerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(symbol);
		security.setMinStepSize(1d);
		security.setMinStepPrice(1d);
		security2 = control.createMock(EditableSecurity.class);
		eventType = (EventTypeSI) security.OnTrade();
		eventType2 = control.createMock(EventTypeSI.class);
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
		Tick
		tick1 = new Tick(new DateTime(2013, 11, 20, 0, 54, 39, 1), 125d, 10d),
		tick2 = new Tick(new DateTime(2014, 11, 20, 1,  4,  2, 9), 132d, 15d); 
				
		BMUtils utils = new BMUtils();
		Trade trade1 = utils.tradeFromTick(tick1, security);
		Trade trade2 = utils.tradeFromTick(tick2, security);
		
		SecurityTradeEvent
		e1 = new SecurityTradeEvent(eventType, security, trade1),
		e2 = new SecurityTradeEvent(eventType2, security, trade1),
		e3 = new SecurityTradeEvent(eventType, security2, trade1),
		e4 = new SecurityTradeEvent(eventType, security, trade2);

		assertTrue(event.equals(e1));
		assertFalse(event.equals(e2));
		assertFalse(event.equals(e3));
		assertFalse(event.equals(e4));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("SecurityTradeEvent[" + trade.toString() + "]", event.toString());
	}

}
