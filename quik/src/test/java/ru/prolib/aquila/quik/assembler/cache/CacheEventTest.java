package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import ru.prolib.aquila.core.*;
import org.junit.*;

public class CacheEventTest {
	private IMocksControl control;
	private EventType type1, type2;
	private CacheEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new CacheEvent(type1, true);
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(event.equals(event));
		assertTrue(event.equals(new CacheEvent(type1, true)));
		assertFalse(event.equals(new CacheEvent(type2, true)));
		assertFalse(event.equals(new CacheEvent(type1, false)));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}

}
