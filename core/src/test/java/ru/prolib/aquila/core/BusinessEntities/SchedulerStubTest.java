package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchedulerStubTest {
	private IMocksControl control;
	private TimeStrategy timeStrategyMock;
	private Runnable runnable;
	private SchedulerStub scheduler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeStrategyMock = control.createMock(TimeStrategy.class);
		runnable = control.createMock(Runnable.class);
		scheduler = new SchedulerStub();
	}
	
	@After
	public void tearDown() throws Exception {
		control.resetToNice();
		scheduler.close();
	}
	
	private Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private CloseableIteratorStub<Instant> createIterator() {
		return new CloseableIteratorStub<Instant>();
	}
	
	@Test
	public void testGetCurrentTime_FixedTimeStrategy() {
		assertEquals(Instant.EPOCH, scheduler.getCurrentTime());
		
		scheduler.setFixedTime(T("1997-09-15T00:00:00Z"));
		
		assertEquals(T("1997-09-15T00:00:00Z"), scheduler.getCurrentTime());
	}
	
	@Test
	public void testGetCurrentTime_CustomTimeStrategy() {
		expect(timeStrategyMock.getTime()).andReturn(T("1998-07-11T00:00:00Z"));
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);
		
		assertEquals(T("1998-07-11T00:00:00Z"), scheduler.getCurrentTime());
		
		control.verify();
	}

	@Test
	public void testGetCurrentTime_IterableTimeStrategy() throws Exception {
		scheduler.setIterableTimeStrategy(createIterator()
			.add(T("2015-01-01T00:00:00Z"))
			.add(T("2015-12-31T00:00:00Z"))
			.add(T("2016-07-31T02:11:00Z")));

		assertEquals(T("2015-01-01T00:00:00Z"), scheduler.getCurrentTime());
		assertEquals(T("2015-12-31T00:00:00Z"), scheduler.getCurrentTime());
		assertEquals(T("2016-07-31T02:11:00Z"), scheduler.getCurrentTime());
	}
	
	@Test
	public void testSchedule_RI() {
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);

		TaskHandler actual = scheduler.schedule(runnable, T("1234-10-01T00:00:00Z"));
		
		control.verify();
		assertEquals(SchedulerStubTask.atTime(T("1234-10-01T00:00:00Z"), runnable), actual);
	}

	@Test
	public void testSchedule_RIL() {
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);

		TaskHandler actual = scheduler.schedule(runnable, T("2016-10-20T00:00:00Z"), 800L);
		
		control.verify();
		assertEquals(SchedulerStubTask.atTimePeriodic(T("2016-10-20T00:00:00Z"),
				800L, runnable), actual);
	}
	
	@Test
	public void testSchedule_RL() {
		expect(timeStrategyMock.getTime()).andReturn(T("2009-01-01T00:30:00Z"));
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);
		
		TaskHandler actual = scheduler.schedule(runnable, 2100L);
		
		control.verify();
		assertEquals(SchedulerStubTask.withDelay(T("2009-01-01T00:30:02.1Z"),
				2100L, runnable), actual);
	}
	
	@Test
	public void testSchedule_RLL() {
		expect(timeStrategyMock.getTime()).andReturn(T("2009-01-01T00:30:40Z"));
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);
		
		TaskHandler actual = scheduler.schedule(runnable, 400L, 1000L);
		
		control.verify();
		assertEquals(SchedulerStubTask.withDelayPeriodic(T("2009-01-01T00:30:40.4Z"),
				400L, 1000L, runnable), actual);
	}

	@Test
	public void testScheduleAtFixedRate_RIL() {
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);

		TaskHandler actual = scheduler.scheduleAtFixedRate(runnable, T("2015-01-01T23:50:00Z"), 1000L);
		
		control.verify();
		assertEquals(SchedulerStubTask.atTimeFixedRate(T("2015-01-01T23:50:00Z"),
				1000L, runnable), actual);
	}

	@Test
	public void testScheduleAtFixedRate_RLL() {
		expect(timeStrategyMock.getTime()).andReturn(T("2015-07-12T00:00:00Z"));
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);
		
		TaskHandler actual = scheduler.scheduleAtFixedRate(runnable, 5000L, 1000L);
		
		control.verify();
		assertEquals(SchedulerStubTask.withDelayFixedRate(T("2015-07-12T00:00:05Z"),
				5000L, 1000L, runnable), actual);
	}
	
	@Test
	public void testClose() throws Exception {
		timeStrategyMock.close();
		control.replay();
		scheduler.setTimeStrategy(timeStrategyMock);
		scheduler.schedule(runnable, T("2015-01-01T00:00:00Z"));
		scheduler.schedule(runnable, T("2015-01-01T00:00:01Z"));
		
		scheduler.close();
		
		control.verify();
		assertEquals(new ArrayList<>(), scheduler.getScheduledTasks());
	}
	
	@Test
	public void testGetScheduledTasks() {
		TaskHandler task1 = scheduler.schedule(runnable, T("2015-01-01T00:00:00Z")),
				task2 = scheduler.schedule(runnable, T("2011-01-01T00:00:00Z")),
				task3 = scheduler.schedule(runnable, T("2001-01-01T00:00:00Z")),
				task4 = scheduler.schedule(runnable, T("2016-07-30T00:00:00Z"));
		
		List<TaskHandler> expected = new ArrayList<>();
		expected.add(task3);
		expected.add(task2);
		expected.add(task1);
		expected.add(task4);
		assertEquals(expected, scheduler.getScheduledTasks());
	}
	
	@Test
	public void testClearScheduledTasks() {
		scheduler.schedule(runnable, T("2015-01-01T00:00:00Z"));
		scheduler.schedule(runnable, T("2011-01-01T00:00:00Z"));
		scheduler.schedule(runnable, T("2001-01-01T00:00:00Z"));
		scheduler.schedule(runnable, T("2016-07-30T00:00:00Z"));
		
		scheduler.clearScheduledTasks();
		
		List<TaskHandler> expected = new ArrayList<>();
		assertEquals(expected, scheduler.getScheduledTasks());
	}

}
