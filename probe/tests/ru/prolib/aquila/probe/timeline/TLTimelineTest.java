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
	private TLSimulationStrategy simulation;
	private TLEventQueue queue;
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
		queue = control.createMock(TLEventQueue.class);
		simulation = control.createMock(TLSimulationStrategy.class);
		timeline = new TLTimeline(sources, queue, simulation);
	}
	
	@Test
	public void testGetPOA() throws Exception {
		expect(queue.getPOA()).andStubReturn(interval.getStart());
		control.replay();
		
		assertEquals(interval.getStart(), timeline.getPOA());
		
		control.verify();
	}
	
	@Test
	public void testPushEvent2() throws Exception {
		DateTime time = new DateTime(2013, 12, 31, 0, 0, 0, 0);
		Runnable procedure = control.createMock(Runnable.class);
		queue.pushEvent(eq(new TLEvent(time, procedure)));
		control.replay();
		
		timeline.pushEvent(time, procedure);
		
		control.verify();
	}
	
	@Test
	public void testPushEvent1() throws Exception {
		TLEvent event = control.createMock(TLEvent.class);
		queue.pushEvent(same(event));
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
	public void testFinish() throws Exception {
		sources.close();
		control.replay();
		
		timeline.finish();
		
		control.verify();
	}
	
	@Test
	public void testRunning() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPaused() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testFinished() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPause() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testRunTo() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testRun() throws Exception {
		fail("Not yet implemented");
	}

}
