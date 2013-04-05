package ru.prolib.aquila.core;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedHashMap;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

/**
 * 2012-04-28<br>
 * $Id: CompositeEventGeneratorImplTest.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class CompositeEventGeneratorImplTest {
	private IMocksControl control;
	private CompositeEventType type;
	private LinkedHashMap<EventType, Event> state;
	private Event event;
	private CompositeEventGenerator generator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(CompositeEventType.class);
		state = new LinkedHashMap<EventType, Event>();
		event = control.createMock(Event.class);
		generator = new CompositeEventGeneratorImpl();
	}
	
	@Test
	public void testGenerateEvent() throws Exception {
		CompositeEvent e = (CompositeEvent) generator.generateEvent(type,
				state, event);
		assertNotNull(e);
		assertSame(state, e.getState());
		assertSame(type, e.getType());
	}

}
