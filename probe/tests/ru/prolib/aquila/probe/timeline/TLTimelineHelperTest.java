package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLTimelineHelperTest {
	private static int lastNum = 0;
	private static DateTime startTime = new DateTime(2014, 1, 30, 0, 0, 0, 0);
	
	static class EventSourceT1 implements TLEventSource {
		private final int id; 
		private final LinkedList<TLEvent> events;
		public EventSourceT1(LinkedList<TLEvent> events) {
			super();
			this.events = events;
			id = lastNum ++;
		}
		@Override public TLEvent pullEvent() throws TLException {
			return events.size() > 0 ? events.removeFirst() : null;
		}
		@Override public void close() { }
		@Override public boolean closed() { return false; }
		@Override public String toString() {
			return "EventSourceT1(" + id + ")";
		};
	}
	
	static class EventSourceT2 implements TLEventSource {
		@Override public TLEvent pullEvent() { return null; }
		@Override public void close() { }
		@Override public boolean closed() { return true; }
	}
	
	static class P implements Runnable {
		private final int id;
		P(int id) {
			super();
			this.id = id;
		}
		@Override public void run() { }
		@Override public String toString() { return Integer.toString(id); }
	}
	
	private IMocksControl control;
	private TLTimelineHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = new TLTimelineHelper();
		control = createStrictControl();
	}
	
	static TLEventSource toSource(TLEvent[] events) {
		LinkedList<TLEvent> list = new LinkedList<TLEvent>();
		for ( TLEvent e : events ) {
			list.add(e);
		}
		return new EventSourceT1(list);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testPullEvents() throws Exception {
		TLEvent events[][] = {
				{
					new TLEvent(startTime, new P(1)),			// # 0.01
					new TLEvent(startTime, new P(2)),			// # 0.04
					new TLEvent(startTime.plus(1), new P(3)),	// # 0.06
					new TLEvent(startTime.plus(10), new P(4)),	// # 1.07
					new TLEvent(startTime.plus(10), new P(5)),	// # 2.10
					new TLEvent(startTime.plus(18), new P(6)),	// # 2.12
				}, {
					new TLEvent(startTime, new P(7)),			// # 0.02
					new TLEvent(startTime.plus(1), new P(8)),	// # 0.05
					new TLEvent(startTime.plus(1), new P(9)),	// # 1.08
					new TLEvent(startTime.plus(18), new P(10)),	// # 1.09
				}, {
					
				}, {
					new TLEvent(startTime.plus(10), new P(11)),	// # 0.03
					new TLEvent(startTime.plus(18), new P(12)),	// # 2.11
				},
		};
		TLEventSources sources = new TLEventSources();
		sources.registerSource(toSource(events[0]));
		sources.registerSource(new EventSourceT2());
		sources.registerSource(toSource(events[1]));
		sources.registerSource(toSource(events[2]));
		sources.registerSource(toSource(events[3]));
		
		List expected[] = {
				new Vector(),
				new Vector(),
				new Vector(),
		};
		expected[0].add(events[0][0]);
		expected[0].add(events[1][0]);
		expected[0].add(events[3][0]);
		expected[0].add(events[0][1]);
		expected[0].add(events[1][1]);
		expected[0].add(events[0][2]);
		expected[1].add(events[0][3]);
		expected[1].add(events[1][2]);
		expected[1].add(events[1][3]);
		expected[2].add(events[0][4]);
		expected[2].add(events[3][1]);
		expected[2].add(events[0][5]);
		
		DateTime POAs[] = {
				startTime,
				startTime.plus(1),
				startTime.plus(10),
		};
		
		for ( int i = 0; i < expected.length; i ++ ) {
			String msg = "At #" + i;
			assertEquals(msg, expected[i], helper.pullEvents(POAs[i], sources));
		}
		assertEquals(new Vector(), helper.pullEvents(startTime.plus(19), sources));
	}
	
	@Test
	public void testPushEvents() throws Exception {
		List<TLEvent> events = new Vector<TLEvent>();
		events.add(new TLEvent(startTime, new P(1)));
		events.add(new TLEvent(startTime, new P(2)));
		events.add(new TLEvent(startTime, new P(3)));
		events.add(new TLEvent(startTime, new P(4)));
		events.add(new TLEvent(startTime, new P(5)));
		TLEventCache timeline = control.createMock(TLEventCache.class);
		for ( TLEvent event : events ) {
			timeline.pushEvent(same(event));
		}
		control.replay();
		
		helper.pushEvents(events, timeline);
		
		control.verify();
	}

}
