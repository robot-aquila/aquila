package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventError;

/**
 * 2012-11-17<br>
 * $Id: IBEventErrorTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventErrorTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBEventError event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		event = new IBEventError(type, 1, 200, "test");
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct2() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		IBEventError event2 = new IBEventError(type2, event);
		assertSame(type2, event2.getType());
		assertEquals(1, event2.getReqId());
		assertEquals(200, event2.getCode());
		assertEquals("test", event2.getMessage());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("Req#1 [200] test", event.toString());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<Integer> vId = new Variant<Integer>(vType)
			.add(1)
			.add(10);
		Variant<Integer> vCode = new Variant<Integer>(vId)
			.add(200)
			.add(500);
		Variant<String> vMsg = new Variant<String>(vCode)
			.add("test")
			.add("unknown");
		int foundCnt = 0;
		IBEventError found = null, x = null;
		do {
			x = new IBEventError(vType.get(),vId.get(),vCode.get(),vMsg.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vMsg.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(1, found.getReqId());
		assertEquals(200, found.getCode());
		assertEquals("test", found.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}

}
