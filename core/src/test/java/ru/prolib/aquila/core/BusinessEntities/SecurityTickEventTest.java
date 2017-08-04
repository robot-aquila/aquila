package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;

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
	private Instant time;
	private Tick tick;
	private SecurityTickEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
		eventType = new EventTypeImpl();
		time = Instant.parse("2017-08-04T20:55:00Z");
		tick = Tick.of(TickType.TRADE, 315.0d, 1000);
		event = new SecurityTickEvent(eventType, security, time, tick);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType, event.getType());
		assertSame(security, event.getSecurity());
		assertEquals(time, event.getTime());
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
