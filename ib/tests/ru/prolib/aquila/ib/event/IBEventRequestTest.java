package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventRequest;

/**
 * 2012-11-17<br>
 * $Id: IBEventRequestTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventRequestTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBEventRequest event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		event = new IBEventRequest(type, 1);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<Integer> vId = new Variant<Integer>(vType)
			.add(1)
			.add(5);
		int foundCnt = 0;
		IBEventRequest found = null, x = null;
		do {
			x = new IBEventRequest(vType.get(), vId.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vId.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(1, found.getReqId());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}

}
