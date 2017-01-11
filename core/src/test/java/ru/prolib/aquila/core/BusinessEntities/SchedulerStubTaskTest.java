package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SchedulerStubTaskTest {
	private IMocksControl control;
	private Runnable runnable;
	private SchedulerStubTask task;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		runnable = control.createMock(Runnable.class);
	}
	
	@After
	public void tearDown() throws Exception {
		task = null;
	}

	@Test
	public void testCtor5() {
		task = new SchedulerStubTask(SchedulerTaskType.AT_TIME_FIXEDRATE,
				Instant.parse("2001-07-12T13:45:00Z"), 100L, 500L, runnable);
		
		assertEquals(SchedulerTaskType.AT_TIME_FIXEDRATE, task.getType());
		assertEquals(Instant.parse("2001-07-12T13:45:00Z"), task.getTime());
		assertEquals(new Long(100L), task.getDelay());
		assertEquals(new Long(500L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());
	}
	
	@Test
	public void testAtTime() {
		task = SchedulerStubTask.atTime(Instant.parse("2015-12-31T00:00:00Z"), runnable);
		
		assertEquals(SchedulerTaskType.AT_TIME, task.getType());
		assertEquals(Instant.parse("2015-12-31T00:00:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertNull(task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testAtTime_TimeString() {
		task = SchedulerStubTask.atTime("2005-12-31T00:00:00Z", runnable);

		assertEquals(SchedulerTaskType.AT_TIME, task.getType());
		assertEquals(Instant.parse("2005-12-31T00:00:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertNull(task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());
	}
	
	@Test
	public void testAtTimeSP() {
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		SPRunnable runnableMock = control.createMock(SPRunnable.class);
		
		task = SchedulerStubTask.atTimeSP(schedulerMock, Instant.parse("2015-01-01T00:00:00Z"), runnableMock);
		
		assertEquals(SchedulerTaskType.AT_TIME, task.getType());
		assertEquals(Instant.parse("2015-01-01T00:00:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertNull(task.getPeriod());
		assertEquals(new SPRunnableTaskHandler(schedulerMock, runnableMock), task.getRunnable());
		assertFalse(task.isCancelled());
	}
	
	@Test
	public void testAtTimeSP_TimeString() {
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		SPRunnable runnableMock = control.createMock(SPRunnable.class);
		
		task = SchedulerStubTask.atTimeSP(schedulerMock, "2017-01-01T00:00:00Z", runnableMock);
		
		assertEquals(SchedulerTaskType.AT_TIME, task.getType());
		assertEquals(Instant.parse("2017-01-01T00:00:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertNull(task.getPeriod());
		assertEquals(new SPRunnableTaskHandler(schedulerMock, runnableMock), task.getRunnable());
		assertFalse(task.isCancelled());
	}
	
	@Test
	public void testAtTimePeriodic() {
		task = SchedulerStubTask.atTimePeriodic(Instant.parse("2015-12-31T00:00:00Z"), 1000L, runnable);
		
		assertEquals(SchedulerTaskType.AT_TIME_PERIODIC, task.getType());
		assertEquals(Instant.parse("2015-12-31T00:00:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertEquals(new Long(1000L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testAtTimePeriodic_TimeString() {
		task = SchedulerStubTask.atTimePeriodic("2015-12-31T00:00:00Z", 1000L, runnable);
		
		assertEquals(SchedulerTaskType.AT_TIME_PERIODIC, task.getType());
		assertEquals(Instant.parse("2015-12-31T00:00:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertEquals(new Long(1000L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testWithDelay() {
		task = SchedulerStubTask.withDelay(Instant.parse("1999-10-15T13:10:00Z"), 5000L, runnable);
		
		assertEquals(SchedulerTaskType.WITH_DELAY, task.getType());
		assertEquals(Instant.parse("1999-10-15T13:10:00Z"), task.getTime());
		assertEquals(new Long(5000L), task.getDelay());
		assertNull(task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}

	@Test
	public void testWithDelay_TimeString() {
		task = SchedulerStubTask.withDelay("1999-10-15T13:10:00Z", 5000L, runnable);
		
		assertEquals(SchedulerTaskType.WITH_DELAY, task.getType());
		assertEquals(Instant.parse("1999-10-15T13:10:00Z"), task.getTime());
		assertEquals(new Long(5000L), task.getDelay());
		assertNull(task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testWithDelayPeriodic() {
		task = SchedulerStubTask.withDelayPeriodic(Instant.parse("1999-10-15T13:10:00Z"),
				5000L, 500L, runnable);
		
		assertEquals(SchedulerTaskType.WITH_DELAY_PERIODIC, task.getType());
		assertEquals(Instant.parse("1999-10-15T13:10:00Z"), task.getTime());
		assertEquals(new Long(5000L), task.getDelay());
		assertEquals(new Long(500L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testWithDelayPeriodic_TimeString() {
		task = SchedulerStubTask.withDelayPeriodic("1999-10-15T13:10:00Z", 5000L, 500L, runnable);
		
		assertEquals(SchedulerTaskType.WITH_DELAY_PERIODIC, task.getType());
		assertEquals(Instant.parse("1999-10-15T13:10:00Z"), task.getTime());
		assertEquals(new Long(5000L), task.getDelay());
		assertEquals(new Long(500L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testAtTimeFixedRate() {
		task = SchedulerStubTask.atTimeFixedRate(Instant.parse("1999-10-15T13:10:00Z"), 800L, runnable);
		
		assertEquals(SchedulerTaskType.AT_TIME_FIXEDRATE, task.getType());
		assertEquals(Instant.parse("1999-10-15T13:10:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertEquals(new Long(800L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}

	@Test
	public void testAtTimeFixedRate_TimeString() {
		task = SchedulerStubTask.atTimeFixedRate("1999-10-15T13:10:00Z", 900L, runnable);
		
		assertEquals(SchedulerTaskType.AT_TIME_FIXEDRATE, task.getType());
		assertEquals(Instant.parse("1999-10-15T13:10:00Z"), task.getTime());
		assertNull(task.getDelay());
		assertEquals(new Long(900L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}

	@Test
	public void testWithDelayFixedRate() {
		task = SchedulerStubTask.withDelayFixedRate(Instant.parse("2000-10-15T13:10:00Z"), 250L, 800L, runnable);
		
		assertEquals(SchedulerTaskType.WITH_DELAY_FIXEDRATE, task.getType());
		assertEquals(Instant.parse("2000-10-15T13:10:00Z"), task.getTime());
		assertEquals(new Long(250L), task.getDelay());
		assertEquals(new Long(800L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}

	@Test
	public void testWithDelayFixedRate_TimeString() {
		task = SchedulerStubTask.withDelayFixedRate("2000-10-15T13:10:00Z", 250L, 800L, runnable);
		
		assertEquals(SchedulerTaskType.WITH_DELAY_FIXEDRATE, task.getType());
		assertEquals(Instant.parse("2000-10-15T13:10:00Z"), task.getTime());
		assertEquals(new Long(250L), task.getDelay());
		assertEquals(new Long(800L), task.getPeriod());
		assertSame(runnable, task.getRunnable());
		assertFalse(task.isCancelled());		
	}
	
	@Test
	public void testCompareTo() {
		SchedulerStubTask task1, task2, task3, task4;
		task1 = new SchedulerStubTask(null, Instant.parse("2016-07-30T00:00:00.000Z"),  null, null, runnable);
		task2 = new SchedulerStubTask(null, Instant.parse("2016-07-30T00:00:00.000Z"),  null, null, runnable);
		task3 = new SchedulerStubTask(null, Instant.parse("2016-07-30T00:00:00.001Z"),  null, null, runnable);
		task4 = new SchedulerStubTask(null, Instant.parse("2016-07-29T23:59:59.999Z"),  null, null, runnable);
		
		assertEquals( 0, task1.compareTo(task1));
		assertEquals( 0, task1.compareTo(task2));
		assertEquals( 0, task2.compareTo(task1));
		assertEquals(-1, task1.compareTo(task3));
		assertEquals( 1, task1.compareTo(task4));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		task = SchedulerStubTask.atTime("2015-09-01T00:00:00Z", runnable);
		
		assertTrue(task.equals(task));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
	}
	
	@Test
	public void testEquals() {
		task = new SchedulerStubTask(SchedulerTaskType.WITH_DELAY_FIXEDRATE,
				Instant.parse("2016-07-30T00:00:00.000Z"),  2000L, 180L, runnable);
		Variant<SchedulerTaskType> vType = new Variant<SchedulerTaskType>()
				.add(SchedulerTaskType.WITH_DELAY_FIXEDRATE)
				.add(SchedulerTaskType.AT_TIME);
		Variant<Instant> vTime = new Variant<Instant>(vType)
				.add(Instant.parse("2016-07-30T00:00:00.000Z"))
				.add(Instant.parse("2013-09-23T00:00:00.000Z"));
		Variant<Long> vDelay = new Variant<Long>(vTime)
				.add(2000L)
				.add(1000L)
				.add(null);
		Variant<Long> vPeriod = new Variant<Long>(vDelay)
				.add(180L)
				.add(200L)
				.add(null);
		Variant<Runnable> vRun = new Variant<Runnable>(vPeriod)
				.add(runnable)
				.add(control.createMock(Runnable.class));
		Variant<Boolean> vCancel = new Variant<Boolean>(vRun)
				.add(false)
				.add(true);
		Variant<?> iterator = vCancel;
		int foundCnt = 0;
		SchedulerStubTask found = null, x;
		do {
			x = new SchedulerStubTask(vType.get(), vTime.get(), vDelay.get(), vPeriod.get(), vRun.get());
			if ( vCancel.get() ) {
				x.cancel();
			}
			if ( task.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(SchedulerTaskType.WITH_DELAY_FIXEDRATE, found.getType());
		assertEquals(Instant.parse("2016-07-30T00:00:00.000Z"), found.getTime());
		assertEquals(new Long(2000L), found.getDelay());
		assertEquals(new Long(180L), found.getPeriod());
		assertEquals(runnable, found.getRunnable());
		assertFalse(found.isCancelled());
	}
	
	@Test
	public void testToString() {
		String tail = runnable.toString() + "]";
		task = SchedulerStubTask.atTime("2015-09-01T00:00:00Z", runnable);
		String expected = "SchedulerStubTask#AT_TIME@2015-09-01T00:00:00Z[" + tail;
		assertEquals(expected, task.toString());
		
		task = SchedulerStubTask.atTimePeriodic("2016-07-30T20:51:00Z", 1500L, runnable);
		expected = "SchedulerStubTask#AT_TIME_PERIODIC@2016-07-30T20:51:00Z[P:1500 " + tail;
		assertEquals(expected, task.toString());
		
		task = SchedulerStubTask.withDelayFixedRate("1997-08-03T00:00:00Z", 5000L, 250L, runnable);	
		expected = "SchedulerStubTask#WITH_DELAY_FIXEDRATE@1997-08-03T00:00:00Z[D:5000 P:250 " + tail;
		assertEquals(expected, task.toString());
		
		task = SchedulerStubTask.withDelay("1764-01-01T00:34:19Z", 7000L, runnable);
		expected = "SchedulerStubTask#WITH_DELAY@1764-01-01T00:34:19Z[D:7000 " + tail;
		assertEquals(expected, task.toString());
	}
	
	@Test
	public void testCancel() {
		task = SchedulerStubTask.atTime("2015-09-01T00:00:00Z", runnable);
		
		assertFalse(task.isCancelled());
		assertTrue(task.cancel());
		assertTrue(task.isCancelled());
		assertFalse(task.cancel());
		assertTrue(task.isCancelled());
	}

}
