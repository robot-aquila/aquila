package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;

public class IBEventTest {
	private IMocksControl control;
	private EventType type1, type2;
	private IBEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new IBEvent(type1);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(event.equals(new IBEvent(type1)));
		assertFalse(event.equals(new IBEvent(type2)));
	}

}
