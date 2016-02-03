package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * 2012-06-01<br>
 * $Id: SecurityTradeEventTest.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class SecurityTickEventTest {
	private IMocksControl control;
	private EditableSecurity security;
	private EventType eventType;
	private Tick tick;
	private SecurityTickEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		eventType = new EventTypeImpl();
		tick = Tick.of(TickType.TRADE, 315.0d, 1000);
		event = new SecurityTickEvent(eventType, security, tick);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType, event.getType());
		assertSame(security, event.getSecurity());
		assertSame(tick, event.getTick());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
		
	@Test
	public void testToString() throws Exception {
		assertEquals(eventType + "[" + tick + "]", event.toString());
	}

}
