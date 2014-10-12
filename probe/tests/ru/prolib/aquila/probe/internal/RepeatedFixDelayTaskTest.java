package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.probe.timeline.TLOutOfIntervalException;
import ru.prolib.aquila.probe.timeline.TLSTimeline;

public class RepeatedFixDelayTaskTest {
	private IMocksControl control;
	private TLSTimeline timeline;
	private Scheduler scheduler;
	private Runnable runnable;
	private RepeatedFixDelayTask task;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeline = control.createMock(TLSTimeline.class);
		scheduler = control.createMock(Scheduler.class);
		runnable = control.createMock(Runnable.class);
		task = new RepeatedFixDelayTask(scheduler, runnable, timeline, 500L);
	}
	
	@Test
	public void testRun_Cancelled() throws Exception {
		expect(scheduler.scheduled(same(runnable))).andReturn(false);
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_Ok() throws Exception {
		expect(scheduler.scheduled(same(runnable))).andReturn(true);
		runnable.run();
		expect(timeline.getPOA())
			.andReturn(new DateTime(2014, 10, 12, 2, 52, 8, 450));
		timeline.schedule(new DateTime(2014, 10, 12, 2, 52, 8, 950), task);
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_EndOfTimelineInterval() throws Exception {
		expect(scheduler.scheduled(same(runnable))).andReturn(true);
		runnable.run();
		expect(timeline.getPOA())
			.andReturn(new DateTime(2014, 10, 12, 2, 52, 8, 450));
		timeline.schedule(new DateTime(2014, 10, 12, 2, 52, 8, 950), task);
		expectLastCall().andThrow(new TLOutOfIntervalException());
		scheduler.cancel(same(runnable));
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(task.equals(task));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		TLSTimeline tline2 = control.createMock(TLSTimeline.class);
		Scheduler sched2 = control.createMock(Scheduler.class);
		Runnable run2 = control.createMock(Runnable.class);
		
		assertTrue(task.equals(
				new RepeatedFixDelayTask(scheduler, runnable, timeline, 500L)));
		assertFalse(task.equals(
				new RepeatedFixDelayTask(scheduler, runnable, tline2, 500L)));
		assertFalse(task.equals(
				new RepeatedFixDelayTask(sched2, runnable, timeline, 500L)));
		assertFalse(task.equals(
				new RepeatedFixDelayTask(scheduler, run2, timeline, 500L)));
		assertFalse(task.equals(
				new RepeatedFixDelayTask(scheduler, runnable, timeline, 123L)));
	}

}
