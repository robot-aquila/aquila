package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.*;

public class TLTimelineTest {
	private static Interval interval;
	private IMocksControl control;
	private TLEventSources sources;
	private TLEventCache cache;
	private TLTimelineHelper helper;
	private TLTimeline timeline;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DateTime start = new DateTime(2014, 1, 30, 0, 0, 0, 0),
			end = new DateTime(2014, 2, 1, 0, 0, 0, 0);
		interval = new Interval(start, end);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sources = control.createMock(TLEventSources.class);
		cache = control.createMock(TLEventCache.class);
		helper = control.createMock(TLTimelineHelper.class);
		timeline = new TLTimeline(sources, cache, helper, interval);
	}
	
	@Test
	public void testConstruct4() throws Exception {
		assertSame(sources, timeline.getEventSources());
		assertSame(cache, timeline.getEventCache());
		assertSame(helper, timeline.getHelper());
		assertSame(interval, timeline.getInterval());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		timeline = new TLTimeline(sources, cache, interval);
		assertSame(sources, timeline.getEventSources());
		assertSame(cache, timeline.getEventCache());
		assertNotNull(timeline.getHelper());
		assertSame(interval, timeline.getInterval());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		timeline = new TLTimeline(interval);
		assertNotNull(timeline.getEventSources());
		cache = timeline.getEventCache();
		assertNotNull(cache);
		assertEquals(interval.getStart(), cache.getPOA());
		assertNotNull(timeline.getHelper());
		assertSame(interval, timeline.getInterval());
	}
	
	@Test
	public void testGetPOA() throws Exception {
		expect(cache.getPOA()).andStubReturn(interval.getStart());
		control.replay();
		
		assertEquals(interval.getStart(), timeline.getPOA());
		
		control.verify();
	}
	
	@Test
	public void testPushEvent() throws Exception {
		TLEvent event = new TLEvent(interval.getStart(), null);
		timeline.pushEvent(same(event));
		control.replay();
		
		timeline.pushEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testRegisterSource() throws Exception {
		TLEventSource source = control.createMock(TLEventSource.class);
		sources.registerSource(same(source));
		control.replay();
		
		timeline.registerSource(source);
		
		control.verify();
	}
	
	@Test
	public void testRemoveSource() throws Exception {
		TLEventSource source = control.createMock(TLEventSource.class);
		sources.removeSource(same(source));
		control.replay();
		
		timeline.removeSource(source);
		
		control.verify();
	}
	
	@Test
	public void testNextTimeStep_NoEvents() throws Exception {
		List<TLEvent> list = new Vector<TLEvent>();
		expect(cache.getPOA()).andReturn(interval.getStart());
		expect(helper.pullEvents(interval.getStart(), sources)).andReturn(list);
		control.replay();
		
		assertFalse(timeline.nextTimeStep());
		
		control.verify();
	}
	
	@Test
	public void testNextTimeStep_NullStack() throws Exception {
		List<TLEvent> list = new Vector<TLEvent>();
		list.add(new TLEvent(interval.getStart(), null));
		expect(cache.getPOA()).andReturn(interval.getStart());
		expect(helper.pullEvents(interval.getStart(), sources)).andReturn(list);
		helper.pushEvents(same(list), same(cache));
		expect(cache.pullStack()).andReturn(null);
		control.replay();
		
		assertFalse(timeline.nextTimeStep());
		
		control.verify();
	}

	@Test
	public void testNextTimeStep_Ok() throws Exception {
		List<TLEvent> list = new Vector<TLEvent>();
		list.add(new TLEvent(interval.getStart(), null));
		expect(cache.getPOA()).andReturn(interval.getStart());
		expect(helper.pullEvents(interval.getStart(), sources)).andReturn(list);
		helper.pushEvents(same(list), same(cache));
		TLEventStack stack = control.createMock(TLEventStack.class);
		expect(cache.pullStack()).andReturn(stack);
		stack.execute();
		expect(cache.getPOA()).andReturn(new DateTime(2014, 1, 30, 1, 0, 0, 0));
		control.replay();
		
		assertTrue(timeline.nextTimeStep());
		
		control.verify();
	}
	
	@Test
	public void testNextTimeStep_EndOfData() throws Exception {
		List<TLEvent> list = new Vector<TLEvent>();
		list.add(new TLEvent(interval.getStart(), null));
		expect(cache.getPOA()).andReturn(interval.getStart());
		expect(helper.pullEvents(interval.getStart(), sources)).andReturn(list);
		helper.pushEvents(same(list), same(cache));
		TLEventStack stack = control.createMock(TLEventStack.class);
		expect(cache.pullStack()).andReturn(stack);
		stack.execute();
		expect(cache.getPOA()).andReturn(new DateTime(2014, 2, 1, 0, 0, 0, 0));
		control.replay();
		
		assertFalse(timeline.nextTimeStep());
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		sources.close();
		control.replay();
		
		timeline.close();
		
		control.verify();
	}

}
