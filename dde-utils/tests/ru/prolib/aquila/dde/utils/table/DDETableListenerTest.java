package ru.prolib.aquila.dde.utils.table;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.DDETableEvent;
import ru.prolib.aquila.dde.utils.DDETopicEvent;

/**
 * 2012-08-12<br>
 * $Id: DDETableListenerTest.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDETableListenerTest {
	private IMocksControl control;
	private DDETableHandler handler;
	private DDETable table;
	private EventType type;
	private DDETableEvent event;
	private DDETableListener listener;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handler = control.createMock(DDETableHandler.class);
		table = control.createMock(DDETable.class);
		type = control.createMock(EventType.class);
		event = new DDETableEvent(type, "foobar", table);
		listener = new DDETableListener("hello", handler);
	}
	
	@Test
	public void testConstruct() throws Exception {
		Object fixture[][] = {
				// topic, handler, exception? 
				{ "hello", handler, false },
				{ null,    handler, true  },
				{ "hello", null,    true  },
				{ null,    null,    true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			boolean exception = false;
			String topic = (String)fixture[i][0];
			handler = (DDETableHandler)fixture[i][1];
			try {
				listener = new DDETableListener(topic, handler);
			} catch ( NullPointerException e ) {
				exception = true;
			}
			String msg = "At #" + i;
			assertEquals(msg, (Boolean) fixture[i][2], exception);
			if ( ! exception ) {
				assertEquals(msg, topic, listener.getTopic());
				assertSame(msg, handler, listener.getTableHandler());
			}
		}
	}
	
	@Test
	public void testOnEvent_Ok() throws Exception {
		expect(table.getTopic()).andReturn("hello");
		handler.handle(same(table));
		control.replay();
		
		listener.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfEventIsNull() throws Exception {
		control.replay();
		
		listener.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfDifferentClass() throws Exception {
		control.replay();
		
		listener.onEvent(new DDETopicEvent(type, "service", "topic"));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfDifferentTopic() throws Exception {
		expect(table.getTopic()).andReturn("another topic");
		control.replay();
		
		listener.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_IfExceptionOnHandling() throws Exception {
		DDEException expected = new DDEException("test exception");
		expect(table.getTopic()).andReturn("hello");
		handler.handle(same(table));
		expectLastCall().andThrow(expected);
		control.replay();
		
		listener.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vName = new Variant<String>()
			.add("hello")
			.add("foobar");
		Variant<DDETableHandler> vHndr = new Variant<DDETableHandler>(vName)
			.add(handler)
			.add(control.createMock(DDETableHandler.class));
		int foundCnt = 0;
		DDETableListener found = null;
		do {
			DDETableListener x = new DDETableListener(vName.get(), vHndr.get());
			if ( listener.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vHndr.next() );
		assertEquals(1, foundCnt);
		assertEquals("hello", found.getTopic());
		assertSame(handler, found.getTableHandler());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(listener.equals(listener));
		assertFalse(listener.equals(this));
		assertFalse(listener.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/51819)
			.append("hello")
			.append(handler)
			.toHashCode();
		assertEquals(hashCode, listener.hashCode());
	}

}
