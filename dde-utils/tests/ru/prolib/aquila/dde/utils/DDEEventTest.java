package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;

public class DDEEventTest {
	private IMocksControl control;
	private EventType type1,type2;
	private DDEEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new DDEEvent(type1, "foobar");
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type1, event.getType());
		assertEquals("foobar", event.getService());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfEventTypeIsNull() throws Exception {
		new DDEEvent(null, "foobar");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfServiceIsNull() throws Exception {
		new DDEEvent(type1, null);
	}
	
	@Test
	public void testEquals() throws Exception {
		Object fix[][] = {
			// to compare, expected result
			{ new DDEEvent(type1, "foobar"), true  },
			{ new DDEEvent(type2, "foobar"), false },
			{ new DDEEvent(type1, "barfoo"), false },
			{ null,							 false },
			{ event,						 true  },
			{ this,							 false }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][1], event.equals(fix[i][0]));
		}
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/92627)
			.append(type1)
			.append("foobar")
			.toHashCode();
		assertEquals(hashCode, event.hashCode());
	}

}
