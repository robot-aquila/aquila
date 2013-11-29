package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLSlotTest {
	private IMocksControl control;
	private Indicator indicator;
	private TLEvent evt;
	private TLSlot slot;
	
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
		slot = new TLSlot(evt);
	}
	
	@Test
	public void testGetTime() throws Exception {
		assertEquals(evt.getTime(), slot.getTime());
	}
	
	@Test
	public void testGetEvents() throws Exception {
		Runnable proc = control.createMock(Runnable.class);
		Vector<TLEvent> expected = new Vector<TLEvent>();
		expected.add(evt);
		expected.add(new TLEvent(evt.getTime(), proc));
		expected.add(new TLEvent(evt.getTime(), proc));
		expected.add(new TLEvent(evt.getTime(), proc));

		slot.addEvent(expected.get(1));
		slot.addEvent(expected.get(2));
		slot.addEvent(expected.get(3));
		assertEquals(expected, slot.getEvents());
	}
	
	@Test
	public void testExecuteEvents() throws Exception {
		slot.addEvent(new TLEvent(evt.getTime(), indicate(2)));
		slot.addEvent(new TLEvent(evt.getTime(), indicate(3)));
		slot.addEvent(new TLEvent(evt.getTime(), indicate(4)));
		slot.addEvent(new TLEvent(evt.getTime(), indicate(5)));
		
		indicator.indicate(eq(1));
		indicator.indicate(eq(2));
		indicator.indicate(eq(3));
		indicator.indicate(eq(4));
		indicator.indicate(eq(5));
		control.replay();
		
		slot.executeEvents();
		  
		control.verify();
	}
	
	@Test (expected=NullPointerException.class)
	public void testAddEvent_ThrowsIfNullEvent() throws Exception {
		slot.addEvent(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddEvent_ThrowsIfEventDifferentTime() throws Exception {
		slot.addEvent(new TLEvent(new DateTime(), null));
	}

	@Test
	public void testCompareTo() throws Exception {
		Runnable any = null;
		DateTime t = evt.getTime();
		TLSlot past = new TLSlot(new TLEvent(t.minus(1), any)),
			same = new TLSlot(new TLEvent(t, any)),
			fut = new TLSlot(new TLEvent(t.plus(1), any));
		
		assertEquals( 1, slot.compareTo(null));
		assertEquals( 1, slot.compareTo(past));
		assertEquals( 0, slot.compareTo(same));
		assertEquals(-1, slot.compareTo(fut));
	}

}
