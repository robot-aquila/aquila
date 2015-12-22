package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class ListenOnceTest {
	private IMocksControl control;
	private EventType type;
	private EventListener listener;
	private ListenOnce once;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		listener = control.createMock(EventListener.class);
		once = new ListenOnce(type, listener);
	}
	
	@Test
	public void testStart() throws Exception {
		type.addListener(same(once));
		control.replay();
		
		once.start();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent() throws Exception {
		Event event = new EventImpl(type);
		type.removeListener(same(once));
		listener.onEvent(eq(event));
		control.replay();
		
		once.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(once.equals(once));
		assertFalse(once.equals(null));
		assertFalse(once.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<EventListener> vLstn = new Variant<EventListener>(vType)
			.add(listener)
			.add(control.createMock(EventListener.class));
		Variant<?> iterator = vLstn;
		int foundCnt = 0;
		ListenOnce x, found = null;
		do {
			x = new ListenOnce(vType.get(), vLstn.get());
			if ( once.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getEventType());
		assertSame(listener, found.getListener());
	}

}
