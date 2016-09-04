package ru.prolib.aquila.probe;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.probe.scheduler.SchedulerMode;
import ru.prolib.aquila.probe.scheduler.SchedulerState;

public class SchedulerImplIT {
	
	static class WaitForModeChange implements Observer {
		private final SchedulerMode expectedMode;
		private final CountDownLatch signal;
		
		WaitForModeChange(SchedulerMode expectedMode) {
			this.expectedMode = expectedMode;
			this.signal = new CountDownLatch(1);
		}

		@Override
		public void update(Observable observable, Object unused) {
			SchedulerState state = (SchedulerState) observable;
			if ( state.getMode() == expectedMode ) {
				observable.deleteObserver(this);
				signal.countDown();
			}
		}
		
	}
	
	/**
	 * The class represents a test task.
	 */
	static class TestTask implements Runnable {
		final List<TestTask> executedTaskSequence;
		final Scheduler scheduler;
		final Instant expectedTime;
		final CountDownLatch signal;
		Instant actualTime, realTime;
		
		TestTask(List<TestTask> executedTaskSequence, Scheduler scheduler,
				Instant expectedScheduledTime, CountDownLatch signal)
		{
			this.executedTaskSequence = executedTaskSequence;
			this.scheduler = scheduler;
			this.expectedTime = expectedScheduledTime;
			this.signal = signal;
		}
		
		TestTask(List<TestTask> executedTaskSequence, Scheduler scheduler,
				Instant expectedScheduledTime)
		{
			this(executedTaskSequence, scheduler, expectedScheduledTime, null);
		}

		@Override
		public void run() {
			actualTime = scheduler.getCurrentTime();
			realTime = Instant.now();
			executedTaskSequence.add(this);
			if ( signal != null ) {
				signal.countDown();
			}
		}
		
		@Override
		public String toString() {
			String pfx = getClass().getSimpleName() + "[";
			if ( actualTime != null ) {
				return pfx + "E expected=" + expectedTime +
					" actual=" + actualTime + " real=" + realTime + "]";
			} else {
				return pfx + "S expected=" + expectedTime + "]";
			}
		}
		
	}
	
	/**
	 * Convert UTC time string to instant.
	 * <p>
	 * @param timeString - time string in UTC format
	 * @return instant
	 */
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	/**
	 * Asserts that two instants are equal with max allowed error.
	 * <p>
	 * @param msg - text message
	 * @param expected - expected time
	 * @param actual - actual time
	 * @param delta - max allowed error in milliseconds
	 */
	static void assertTimeEquals(String msg, Instant expected, Instant actual, long delta) {
		long actualDiff = Math.abs(ChronoUnit.MILLIS.between(expected, actual));
		assertThat(msg + " Comparing two instants: " + expected + " and " + actual,
				actualDiff, lessThanOrEqualTo(delta));
	}
	
	/**
	 * Asserts that two instants are equal with max allowed error.
	 * <p>
	 * @param expected - expected time
	 * @param actual - actual time
	 * @param delta - max allowed error in milliseconds
	 */
	static void assertTimeEquals(Instant expected, Instant actual, long delta) {
		assertTimeEquals("", expected, actual, delta);
	}
	
	/**
	 * Asserts that all instants of the time series are close to each other.
	 * <p>
	 * @param expectedBaseTime - the base time (start time)
	 * @param timeSeries - time series
	 * @param delta - max allowed error in milliseconds
	 */
	static void assertTimeCloseToEachOther(Instant expectedBaseTime,
			List<Instant> timeSeries, long delta)
	{
		for ( int i = 0; i < timeSeries.size(); i ++ ) {
			Instant actualTime = timeSeries.get(i);
			assertTimeEquals("At #" + i, expectedBaseTime, actualTime, delta);
			expectedBaseTime = actualTime;
		}
	}
	
	/**
	 * Asserts that all instants of the time series are close to time calculated
	 * by offset in milliseconds.
	 * <p>
	 * @param expectedBaseTime - the base time (start time)
	 * @param timeSeries - time series
	 * @param expectedOffset - an expected offset by the previous element (base time at start)  
	 * @param delta - max allowed error in milliseconds
	 */
	static void assertTimeCloseToOffset(Instant expectedBaseTime,
			List<Instant> timeSeries, List<Long> expectedOffset, long delta)
	{
		for ( int i = 0; i < timeSeries.size(); i ++ ) {
			Instant actualTime = timeSeries.get(i);
			Instant expectedTime = expectedBaseTime.plusMillis(expectedOffset.get(i));
			assertTimeEquals("At #" + i, expectedTime, actualTime, delta);
			expectedBaseTime = actualTime;
		}
	}
	
	/**
	 * Convert list of test tasks to expected execution time series.
	 * <p>
	 * @param tasks - task list
	 * @return time series
	 */
	static List<Instant> getExpectedTimeSeries(List<TestTask> tasks) {
		List<Instant> result = new ArrayList<>();
		for ( TestTask task : tasks ) {
			result.add(task.expectedTime);
		}
		return result;
	}
	
	/**
	 * Convert list of test tasks to actual execution time series.
	 * <p>
	 * @param tasks - task list
	 * @return time series
	 */
	static List<Instant> getActualTimeSeries(List<TestTask> tasks) {
		List<Instant> result = new ArrayList<>();
		for ( TestTask task : tasks ) {
			result.add(task.actualTime);
		}
		return result;
	}
	
	/**
	 * Convert list of test tasks to real execution time series.
	 * <p>
	 * @param tasks - task list
	 * @return time series
	 */
	static List<Instant> getRealTimeSeries(List<TestTask> tasks) {
		List<Instant> result = new ArrayList<>();
		for ( TestTask task : tasks ) {
			result.add(task.realTime);
		}
		return result;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private SchedulerImpl scheduler;
	private CountDownLatch signal;
	private List<TestTask> actualTaskSequence;
	
	/**
	 * Create new test task.
	 * <p> 
	 * @param expectedTimeString - expected execution time string in UTC format
	 * @param signal - signal to set when executed
	 * @return new task instance
	 */
	TestTask TT(String expectedTimeString, CountDownLatch signal) {
		return new TestTask(actualTaskSequence, scheduler, T(expectedTimeString), signal);
	}
	
	/**
	 * Create new test task.
	 * <p>
	 * @param expectedTimeString - expected execution time string in UTC format
	 * @return new task instance
	 */
	TestTask TT(String expectedTimeString) {
		return TT(expectedTimeString, null);
	}
	
	/**
	 * Schedule all tasks.
	 * <p>
	 * @param tasks - task list
	 */
	void scheduleAll(List<TestTask> tasks) {
		for ( TestTask task : tasks ) {
			scheduler.schedule(task, task.expectedTime);
		}
	}

	@Before
	public void setUp() throws Exception {
		actualTaskSequence = new ArrayList<>();
		signal = new CountDownLatch(1);
		scheduler = new SchedulerBuilder()
			.setName("PROBE-TEST")
			.setInitialTime(T("2016-08-31T00:00:00Z"))
			.buildScheduler();
	}
	
	@After
	public void tearDown() throws Exception {
		scheduler.close();
	}
	
	@Test
	public void testExecutionSequence_Speed0() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:01Z"));
		fixture.add(TT("2016-08-31T00:00:10Z"));
		fixture.add(TT("2016-08-31T00:00:25Z"));
		fixture.add(TT("2016-08-31T00:00:40Z"));
		fixture.add(TT("2016-08-31T00:00:55Z"));
		fixture.add(TT("2016-08-31T00:01:12Z"));
		fixture.add(TT("2016-08-31T00:05:15Z", signal));
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture);
		Collections.shuffle(fixture);
		scheduleAll(fixture);
		
		Instant expectedBaseTime = Instant.now();
		scheduler.setModeRun();
		
		assertTrue(signal.await(200, TimeUnit.MILLISECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		assertTimeCloseToEachOther(expectedBaseTime, getRealTimeSeries(actualTaskSequence), 10);
	}
	
	@Test
	public void testExecutionSequence_Speed1() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.005Z"));
		fixture.add(TT("2016-08-31T00:00:00.050Z"));
		fixture.add(TT("2016-08-31T00:00:00.250Z"));
		fixture.add(TT("2016-08-31T00:00:01.000Z"));
		fixture.add(TT("2016-08-31T00:00:05.000Z"));
		fixture.add(TT("2016-08-31T00:00:07.000Z", signal));
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture);
		scheduleAll(fixture);
		
		Instant expectedBaseTime = Instant.now();
		scheduler.setExecutionSpeed(1);
		scheduler.setModeRun();
		
		assertTrue(signal.await(10, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = new ArrayList<>();
		expectedOffset.add(   5L);
		expectedOffset.add(  45L);
		expectedOffset.add( 200L);
		expectedOffset.add( 750L);
		expectedOffset.add(4000L);
		expectedOffset.add(2000L);
		assertTimeCloseToOffset(expectedBaseTime, getRealTimeSeries(actualTaskSequence), expectedOffset, 20);
	}

	@Test
	public void testExecutionSequence_Speed2() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.005Z"));
		fixture.add(TT("2016-08-31T00:00:00.050Z"));
		fixture.add(TT("2016-08-31T00:00:00.250Z"));
		fixture.add(TT("2016-08-31T00:00:01.000Z"));
		fixture.add(TT("2016-08-31T00:00:05.000Z"));
		fixture.add(TT("2016-08-31T00:00:07.000Z", signal));
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture);
		scheduleAll(fixture);
		
		Instant expectedBaseTime = Instant.now();
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun();
		
		assertTrue(signal.await(5, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = new ArrayList<>();
		expectedOffset.add(   2L);
		expectedOffset.add(  42L);
		expectedOffset.add( 100L);
		expectedOffset.add( 375L);
		expectedOffset.add(2000L);
		expectedOffset.add(1000L);
		assertTimeCloseToOffset(expectedBaseTime, getRealTimeSeries(actualTaskSequence), expectedOffset, 20);
	}
	
	@Test
	public void testPauseAndUnpause() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.050Z"));
		fixture.add(TT("2016-08-31T00:00:00.250Z"));
		fixture.add(TT("2016-08-31T00:00:01.000Z"));
		fixture.add(TT("2016-08-31T00:00:05.000Z"));
		fixture.add(TT("2016-08-31T00:00:07.000Z", signal));
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture);
		scheduleAll(fixture);

		Instant expectedBaseTime = Instant.now();
		scheduler.setExecutionSpeed(1);
		scheduler.setModeRun();
		
		Thread.sleep(1500);
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		scheduler.getState().addObserver(wait);
		scheduler.setModeWait();
		assertTrue(wait.signal.await(100L, TimeUnit.MILLISECONDS));
		// It should be exact 1.5 seconds (by sum of ticks) 
		assertEquals(T("2016-08-31T00:00:01.500Z"), scheduler.getCurrentTime());
		Thread.sleep(500);
		scheduler.setModeRun();
		
		assertTrue(signal.await(10, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = new ArrayList<>();
		expectedOffset.add(  50L);
		expectedOffset.add( 200L);
		expectedOffset.add( 750L);
		// pause 500 ms here
		expectedOffset.add(4500L);
		expectedOffset.add(2000L);
		assertTimeCloseToOffset(expectedBaseTime, getRealTimeSeries(actualTaskSequence), expectedOffset, 20);
	}
	
	@Test
	public void testStepByStep() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.050Z"));
		fixture.add(TT("2016-08-31T00:00:00.250Z"));
		fixture.add(TT("2016-08-31T00:00:01.000Z"));
		fixture.add(TT("2016-08-31T00:00:05.000Z"));
		fixture.add(TT("2016-08-31T00:00:07.000Z", signal));
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture);
		scheduleAll(fixture);

		Instant expectedBaseTime = Instant.now();
		scheduler.setModeStep();
		Thread.sleep(500L);
		scheduler.setModeStep();
		Thread.sleep(500L);
		scheduler.setModeStep();
		Thread.sleep(500L);
		scheduler.setModeStep();
		Thread.sleep(500L);
		scheduler.setModeStep();
		
		assertTrue(signal.await(1, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = new ArrayList<>();
		expectedOffset.add(0L);
		expectedOffset.add(500L);
		expectedOffset.add(500L);
		expectedOffset.add(500L);
		expectedOffset.add(500L);
		assertTimeCloseToOffset(expectedBaseTime, getRealTimeSeries(actualTaskSequence), expectedOffset, 20);
	}
	
	@Test
	public void testCutoff() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.050Z"));
		fixture.add(TT("2016-08-31T00:00:00.250Z"));
		fixture.add(TT("2016-08-31T00:00:01.000Z"));
		fixture.add(TT("2016-08-31T00:00:05.000Z"));
		fixture.add(TT("2016-08-31T00:00:07.000Z"));
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture.subList(0, 3));
		scheduleAll(fixture);

		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		scheduler.getState().addObserver(wait);
		Instant expectedBaseTime = Instant.now();
		scheduler.setModeRun(T("2016-08-31T00:00:03Z"));
		
		assertTrue(wait.signal.await(1, TimeUnit.SECONDS));
		assertEquals(T("2016-08-31T00:00:03Z"), scheduler.getCurrentTime());
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		assertTimeCloseToEachOther(expectedBaseTime, getRealTimeSeries(actualTaskSequence), 20);
	}
	
	@Test
	public void testRunningWithoutTasks_Speed1() throws Exception {
		scheduler.setExecutionSpeed(1);
		scheduler.setModeRun();
		
		Thread.sleep(1000);
		assertTimeEquals(T("2016-08-31T00:00:01Z"), scheduler.getCurrentTime(), 120);
		Thread.sleep(1000);
		assertTimeEquals(T("2016-08-31T00:00:02Z"), scheduler.getCurrentTime(), 120);
		Thread.sleep(1000);
		assertTimeEquals(T("2016-08-31T00:00:03Z"), scheduler.getCurrentTime(), 120);
	}
	
	@Test
	public void testRunningWithoutTasks_Speed2() throws Exception {
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun();
		
		Thread.sleep(1000);
		assertTimeEquals(T("2016-08-31T00:00:02Z"), scheduler.getCurrentTime(), 120);
		Thread.sleep(1000);
		assertTimeEquals(T("2016-08-31T00:00:04Z"), scheduler.getCurrentTime(), 120);
		Thread.sleep(1000);
		assertTimeEquals(T("2016-08-31T00:00:06Z"), scheduler.getCurrentTime(), 120);
	}

}
