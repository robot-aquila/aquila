package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;

public class DDETopicEventTest {
	private IMocksControl control;
	private EventType type1,type2;
	private DDETopicEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new DDETopicEvent(type1, "service", "topic");
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type1, event.getType());
		assertEquals("service", event.getService());
		assertEquals("topic", event.getTopic());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfEventTypeIsNull() throws Exception {
		new DDETopicEvent(null, "service", "topic");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfServiceIsNull() throws Exception {
		new DDETopicEvent(type1, null, "topic");
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTopicIsNull() throws Exception {
		new DDETopicEvent(type1, "service", null);
	}
	
	@Test
	public void testEquals() throws Exception {
		Object fix[][] = {
			// to compare, expected result
			{ new DDETopicEvent(type1, "service", "topic"), true  },
			{ new DDETopicEvent(type2, "service", "topic"),	false },
			{ new DDETopicEvent(type1, "service", "bable"), false },
			{ null,							 				false },
			{ event,						 				true  },
			{ this,							 				false }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][1], event.equals(fix[i][0]));
		}
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/90901)
			.append(type1)
			.append("service")
			.append("topic")
			.toHashCode();
		assertEquals(hashCode, event.hashCode());
	}


}
