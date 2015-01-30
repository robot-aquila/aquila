package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLEventStackTest {
	private IMocksControl control;
	private Indicator indicator;
	private TLEvent evt;
	private TLEventStack stack;
	
	interface Indicator { public void indicate(int id); }
	
	private Runnable indicate(final int id) {
		return new Runnable() {
			@Override
			public void run() {
				indicator.indicate(id);
			}
		};
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		indicator = control.createMock(Indicator.class);
		evt = new TLEvent(new DateTime(2013, 11, 29, 16, 1, 5, 0), indicate(1));
		stack = new TLEventStack(evt);
	}
	
	@Test
	public void testGetTime() throws Exception {
		assertEquals(evt.getTime(), stack.getTime());
	}
	
	@Test
	public void testGetEvents() throws Exception {
		Runnable proc = control.createMock(Runnable.class);
		Vector<TLEvent> expected = new Vector<TLEvent>();
		expected.add(evt);
		expected.add(new TLEvent(evt.getTime(), proc));
		expected.add(new TLEvent(evt.getTime(), proc));
		expected.add(new TLEvent(evt.getTime(), proc));

		stack.pushEvent(expected.get(1));
		stack.pushEvent(expected.get(2));
		stack.pushEvent(expected.get(3));
		assertEquals(expected, stack.getEvents());
	}
	
	@Test
	public void testExecute() throws Exception {
		stack.pushEvent(new TLEvent(evt.getTime(), indicate(2)));
		stack.pushEvent(new TLEvent(evt.getTime(), indicate(3)));
		stack.pushEvent(new TLEvent(evt.getTime(), indicate(4)));
		stack.pushEvent(new TLEvent(evt.getTime(), indicate(5)));
		
		indicator.indicate(eq(1));
		indicator.indicate(eq(2));
		indicator.indicate(eq(3));
		indicator.indicate(eq(4));
		indicator.indicate(eq(5));
		control.replay();
		
		stack.execute();
		  
		control.verify();
		assertTrue(stack.executed());
	}
	
	@Test
	public void testExecute_RunsOnce() throws Exception {
		TLEvent e2 = control.createMock(TLEvent.class),
			e3 = control.createMock(TLEvent.class);
		
		expect(e2.getTime()).andStubReturn(evt.getTime());
		expect(e3.getTime()).andStubReturn(evt.getTime());
		indicator.indicate(eq(1));
		e2.execute();
		e3.execute();
		control.replay();
		
		stack.pushEvent(e2);
		stack.pushEvent(e3);
		stack.execute();
		stack.execute();
		
		control.verify();
	}
	
	@Test (expected=NullPointerException.class)
	public void testPushEvent_ThrowsIfNullEvent() throws Exception {
		stack.pushEvent(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testPushEvent_ThrowsIfEventDifferentTime() throws Exception {
		stack.pushEvent(new TLEvent(new DateTime(), null));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testPushEvent_ThrowsIfStackExecuted() throws Exception {
		indicator.indicate(eq(1));
		control.replay();
		
		stack.execute();
		stack.pushEvent(new TLEvent(evt.getTime(), indicate(2)));
	}

	@Test
	public void testCompareTo() throws Exception {
		Runnable any = null;
		DateTime t = evt.getTime();
		TLEventStack past = new TLEventStack(new TLEvent(t.minus(1), any)),
			same = new TLEventStack(new TLEvent(t, any)),
			fut = new TLEventStack(new TLEvent(t.plus(1), any));
		
		assertEquals( 1, stack.compareTo(null));
		assertEquals( 1, stack.compareTo(past));
		assertEquals( 0, stack.compareTo(same));
		assertEquals(-1, stack.compareTo(fut));
	}

}
