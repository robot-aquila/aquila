package ru.prolib.aquila.probe;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final double ERROR_THRESHOLD = 0.12d; // 12% error
	
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
	 * Assert that two instants are equal with max allowed error.
	 * <p>
	 * @param msg - text message
	 * @param expected - expected instant
	 * @param actual - actual instant
	 * @param lowerDelta - this value is used as max allowed error if actual time is before than expected
	 * @param upperDelta - this value is used as max allowed error if actual time is after than expected
	 */
	static void assertTimeEquals(String msg, Instant expected, Instant actual, long lowerDelta, long upperDelta) {
		long actualDelta = ChronoUnit.MILLIS.between(expected, actual);
		long delta = upperDelta;
		if ( actualDelta < 0 ) {
			delta = lowerDelta;
		}
		assertTimeEquals(msg, expected, actual, delta);
	}
	
	static void assertTimeEquals(Instant expected, Instant actual, long lowerDelta, long upperDelta) {
		assertTimeEquals("", expected, actual, lowerDelta, upperDelta);
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
	 * @param deltaFactor - 0.0 to 1.0 factor of max allowed error size based of offset in milliseconds
	 */
	static void assertTimeCloseToOffset(Instant expectedBaseTime,
			List<Instant> timeSeries, List<Long> expectedOffset, double deltaFactor)
	{
		for ( int i = 0; i < timeSeries.size(); i ++ ) {
			Instant actualTime = timeSeries.get(i);
			long offset = expectedOffset.get(i);
			long delta = (long)(deltaFactor * (double)offset);
			if ( delta < 20 ) {
				delta = 20;
			}
			Instant expectedTime = expectedBaseTime.plusMillis(offset);
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
	public void testExecutionSequence_Speed0_CutoffBeforeTasks() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T01:00:00Z"));
		fixture.add(TT("2016-08-31T02:00:00Z"));
		List<TestTask> expectedTaskSequence = new ArrayList<>();
		scheduleAll(fixture);
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		SchedulerState state = scheduler.getState();
		state.addObserver(wait);
		
		scheduler.setModeRun(T("2016-08-31T00:30:00Z"));
		
		assertTrue(wait.signal.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T00:30:00Z"), scheduler.getCurrentTime());
	}

	@Test
	public void testExecutionSequence_Speed0_CutoffAtTask() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T01:00:00Z"));
		fixture.add(TT("2016-08-31T02:00:00Z")); // this shouldn't be executed
		fixture.add(TT("2016-08-31T03:00:00Z"));
		List<TestTask> expected_task_sequence = new ArrayList<>(fixture.subList(0, 1));
		scheduleAll(fixture);
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		SchedulerState state = scheduler.getState();
		state.addObserver(wait);
		
		scheduler.setModeRun(T("2016-08-31T02:00:00Z"));
		
		assertTrue(wait.signal.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T02:00:00Z"), scheduler.getCurrentTime());
	}
	
	@Test
	public void testExecutionSequence_Speed0_CutoffBtwTasks() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T01:00:00Z"));
		fixture.add(TT("2016-08-31T02:00:00Z"));
		fixture.add(TT("2016-08-31T03:00:00Z"));
		List<TestTask> expected_task_sequence = new ArrayList<>(fixture.subList(0, 2));
		scheduleAll(fixture);
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		SchedulerState state = scheduler.getState();
		state.addObserver(wait);
		
		scheduler.setModeRun(T("2016-08-31T02:30:00Z"));
		
		assertTrue(wait.signal.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T02:30:00Z"), scheduler.getCurrentTime());
	}
	
	@Test
	public void testExecutionSequence_Speed0_CutoffAfterTasks() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T01:00:00Z"));
		fixture.add(TT("2016-08-31T02:00:00Z"));
		fixture.add(TT("2016-08-31T03:00:00Z"));
		List<TestTask> expected_task_sequence = new ArrayList<>(fixture.subList(0, 3));
		scheduleAll(fixture);
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		SchedulerState state = scheduler.getState();
		state.addObserver(wait);
		
		scheduler.setModeRun(T("2016-08-31T03:30:00Z"));
		
		assertTrue(wait.signal.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T03:30:00Z"), scheduler.getCurrentTime());
	}
	
	@Test
	public void testExecutionSequence_Speed0_CutoffWoTasks() throws Exception {
		List<TestTask> expected_task_sequence = new ArrayList<>();
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		SchedulerState state = scheduler.getState();
		state.addObserver(wait);
		
		scheduler.setModeRun(T("2016-08-31T05:30:00Z"));
		
		assertTrue(wait.signal.await(100, TimeUnit.MILLISECONDS));
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T05:30:00Z"), scheduler.getCurrentTime());
	}
	
	@Test
	public void testExecutionSequence_Speed1() throws Exception {
		List<TestTask> fixture = Arrays.asList(
				TT("2016-08-31T00:00:00.600Z"),
				TT("2016-08-31T00:00:01.000Z"),
				TT("2016-08-31T00:00:01.800Z"),
				TT("2016-08-31T00:00:02.500Z"),
				TT("2016-08-31T00:00:05.000Z"),
				TT("2016-08-31T00:00:07.000Z", signal)
			);
		List<TestTask> expectedTaskSequence = new ArrayList<>(fixture);
		scheduleAll(fixture);
		
		Instant expectedBaseTime = Instant.now();
		scheduler.setExecutionSpeed(1);
		scheduler.setModeRun();
		
		assertTrue(signal.await(10, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = new ArrayList<>();
		expectedOffset.add( 600L);
		expectedOffset.add( 400L);
		expectedOffset.add( 800L);
		expectedOffset.add( 700L);
		expectedOffset.add(2500L);
		expectedOffset.add(2000L);
		assertTimeCloseToOffset(
				expectedBaseTime,
				getRealTimeSeries(actualTaskSequence),
				expectedOffset,
				ERROR_THRESHOLD
			);
	}

	@Test
	public void testExecutionSequence_Speed2() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.200Z"));
		fixture.add(TT("2016-08-31T00:00:00.600Z"));
		fixture.add(TT("2016-08-31T00:00:01.500Z"));
		fixture.add(TT("2016-08-31T00:00:02.200Z"));
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
		expectedOffset.add( 100L);
		expectedOffset.add( 200L);
		expectedOffset.add( 450L);
		expectedOffset.add( 350L);
		expectedOffset.add(1400L);
		expectedOffset.add(1000L);
		assertTimeCloseToOffset(
				expectedBaseTime,
				getRealTimeSeries(actualTaskSequence),
				expectedOffset,
				ERROR_THRESHOLD
			);
	}
	
	@Test
	public void testExecutionSequence_Speed2_CutoffBeforeTasks() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.200Z"));
		fixture.add(TT("2016-08-31T00:00:00.300Z"));
		fixture.add(TT("2016-08-31T00:00:00.400Z"));
		List<TestTask> expected_task_sequence = new ArrayList<>();
		SchedulerState state = scheduler.getState();
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		
		Instant start_time = Instant.now();
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun(T("2016-08-31T00:00:00.100Z"));
		
		assertTrue(wait.signal.await(100, TimeUnit.MILLISECONDS));
		Instant end_time = Instant.now();
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(T("2016-08-31T00:00:00.100Z"), scheduler.getCurrentTime());
		List<Instant> actual_rt_series = new ArrayList<>();
		actual_rt_series.add(end_time);
		List<Long> expected_rt_offset = new ArrayList<>();
		expected_rt_offset.add(50L);
		assertTimeCloseToOffset(start_time, actual_rt_series, expected_rt_offset, ERROR_THRESHOLD);
		//System.out.println("start_time: " + start_time + " end_time=" + end_time);
	}
	
	@Test
	public void testExecutionSequence_Speed2_CutoffAtTask() throws Exception {
		List<TestTask> fixture = Arrays.asList(
				TT("2016-08-31T00:00:01.000Z"),
				TT("2016-08-31T00:00:02.200Z"),
				TT("2016-08-31T00:00:03.500Z")
			);
		List<TestTask> expected_task_sequence = new ArrayList<>(fixture.subList(0, 2));
		scheduleAll(fixture);
		SchedulerState state = scheduler.getState();
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		
		Instant start_time = Instant.now(), cutoff_time = T("2016-08-31T00:00:03.500Z");
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun(cutoff_time);
		
		assertTrue(wait.signal.await(5, TimeUnit.SECONDS));
		Instant end_time = Instant.now();
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(cutoff_time, scheduler.getCurrentTime());
		// How the simulation differs of expected simulation results
		List<Instant> expected_st_series = getExpectedTimeSeries(actualTaskSequence);
		expected_st_series.add(cutoff_time);
		List<Instant> actual_st_series = getActualTimeSeries(actualTaskSequence);
		actual_st_series.add(scheduler.getCurrentTime());
		assertEquals(expected_st_series, actual_st_series);
		// How the simulation differs of expected realtime results
		List<Instant> actual_rt_series = getRealTimeSeries(actualTaskSequence);
		actual_rt_series.add(end_time);
		List<Long> expected_rt_offset = new ArrayList<>();
		expected_rt_offset.add(500L);
		expected_rt_offset.add(600L);
		expected_rt_offset.add(650L);
		assertTimeCloseToOffset(start_time, actual_rt_series, expected_rt_offset, ERROR_THRESHOLD);
		//System.out.println("start_time: " + start_time + " end_time=" + end_time);
	}
	
	@Test
	public void testExecutionSequence_Speed2_CutoffBtwTasks() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:01.000Z"));
		fixture.add(TT("2016-08-31T00:00:01.800Z"));
		fixture.add(TT("2016-08-31T00:00:03.000Z")); // shouldn't be executed
		List<TestTask> expected_task_sequence = new ArrayList<>(fixture.subList(0, 2));
		scheduleAll(fixture);
		SchedulerState state = scheduler.getState();
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		
		Instant start_time = Instant.now(), cutoff_time = T("2016-08-31T00:00:02.500Z");
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun(cutoff_time);

		assertTrue(wait.signal.await(5, TimeUnit.SECONDS));
		Instant end_time = Instant.now();
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(cutoff_time, scheduler.getCurrentTime());
		// How the simulation differs of expected simulation results
		List<Instant> expected_st_series = getExpectedTimeSeries(actualTaskSequence);
		expected_st_series.add(cutoff_time);
		List<Instant> actual_st_series = getActualTimeSeries(actualTaskSequence);
		actual_st_series.add(scheduler.getCurrentTime());
		assertEquals(expected_st_series, actual_st_series);
		// How the simulation differs of expected realtime results
		List<Instant> actual_rt_series = getRealTimeSeries(actualTaskSequence);
		actual_rt_series.add(end_time);
		List<Long> expected_rt_offset = new ArrayList<>();
		expected_rt_offset.add(500L);
		expected_rt_offset.add(400L);
		expected_rt_offset.add(350L);
		//System.out.println("start_time: " + start_time + " end_time=" + end_time);
		//System.out.println("actual_rt_series: " + actual_rt_series);
		//System.out.println("expected_rt_offset: " + expected_rt_offset);
		assertTimeCloseToOffset(start_time, actual_rt_series, expected_rt_offset, ERROR_THRESHOLD);
	}
	
	@Test
	public void testExecutionSequence_Speed2_CutoffAfterTasks() throws Exception {
		List<TestTask> fixture = new ArrayList<>();
		fixture.add(TT("2016-08-31T00:00:00.400Z"));
		fixture.add(TT("2016-08-31T00:00:01.500Z"));
		fixture.add(TT("2016-08-31T00:00:02.300Z"));
		List<TestTask> expected_task_sequence = new ArrayList<>(fixture);
		scheduleAll(fixture);
		SchedulerState state = scheduler.getState();
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		
		Instant start_time = Instant.now(), cutoff_time = T("2016-08-31T00:00:04.000Z");
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun(cutoff_time);
		
		assertTrue(wait.signal.await(5, TimeUnit.SECONDS));
		Instant end_time = Instant.now();
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(cutoff_time, scheduler.getCurrentTime());
		// How the simulation differs of expected simulation results
		List<Instant> expected_st_series = getExpectedTimeSeries(actualTaskSequence);
		expected_st_series.add(cutoff_time);
		List<Instant> actual_st_series = getActualTimeSeries(actualTaskSequence);
		actual_st_series.add(scheduler.getCurrentTime());
		assertEquals(expected_st_series, actual_st_series);
		
		// How the simulation differs of expected realtime results
		List<Instant> actual_rt_series = getRealTimeSeries(actualTaskSequence);
		actual_rt_series.add(end_time);
		List<Long> expected_rt_offset = new ArrayList<>();
		expected_rt_offset.add(200L);
		expected_rt_offset.add(550L);
		expected_rt_offset.add(400L);
		expected_rt_offset.add(840L);
		assertTimeCloseToOffset(start_time, actual_rt_series, expected_rt_offset, ERROR_THRESHOLD);
	}
	
	@Test
	public void testExecutionSequence_Speed2_CutoffWoTasks() throws Exception {
		List<TestTask> expected_task_sequence = new ArrayList<>();
		SchedulerState state = scheduler.getState();
		WaitForModeChange wait = new WaitForModeChange(SchedulerMode.WAIT);
		state.addObserver(wait);
		
		Instant start_time = Instant.now(), cutoff_time = T("2016-08-31T00:00:03.000Z");
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun(cutoff_time);
		
		assertTrue(wait.signal.await(5, TimeUnit.SECONDS));
		Instant end_time = Instant.now();
		assertEquals(expected_task_sequence, actualTaskSequence);
		assertEquals(SchedulerMode.WAIT, state.getMode());
		assertEquals(cutoff_time, scheduler.getCurrentTime());
		
		// How the simulation differs of expected simulation results
		List<Instant> expected_st_series = getExpectedTimeSeries(actualTaskSequence);
		expected_st_series.add(cutoff_time);
		List<Instant> actual_st_series = getActualTimeSeries(actualTaskSequence);
		actual_st_series.add(scheduler.getCurrentTime());
		assertEquals(expected_st_series, actual_st_series);
		
		// How the simulation differs of expected realtime results
		List<Instant> actual_rt_series = getRealTimeSeries(actualTaskSequence);
		actual_rt_series.add(end_time);
		List<Long> expected_rt_offset = new ArrayList<>();
		expected_rt_offset.add(1500L);
		assertTimeCloseToOffset(start_time, actual_rt_series, expected_rt_offset, ERROR_THRESHOLD);
	}
	
	@Test
	public void testPauseAndUnpause() throws Exception {
		List<TestTask> fixture = Arrays.asList(
				TT("2016-08-31T00:00:00.250Z"),
				TT("2016-08-31T00:00:01.000Z"),
				TT("2016-08-31T00:00:05.000Z"),
				TT("2016-08-31T00:00:07.000Z", signal)
			);
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
		assertTimeEquals(T("2016-08-31T00:00:01.500Z"), scheduler.getCurrentTime(), 300);
		Thread.sleep(500);
		scheduler.setModeRun();
		
		assertTrue(signal.await(10, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = Arrays.asList(250L, 750L, /*pause 500 ms here*/ 4500L, 2000L);
		assertTimeCloseToOffset(
				expectedBaseTime,
				getRealTimeSeries(actualTaskSequence),
				expectedOffset,
				ERROR_THRESHOLD
			);
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
		Thread.sleep(2000L);
		scheduler.setModeStep();
		Thread.sleep(2000L);
		scheduler.setModeStep();
		Thread.sleep(2000L);
		scheduler.setModeStep();
		Thread.sleep(2000L);
		scheduler.setModeStep();
		
		assertTrue(signal.await(5, TimeUnit.SECONDS));
		assertEquals(expectedTaskSequence, actualTaskSequence);
		assertEquals(getExpectedTimeSeries(actualTaskSequence), getActualTimeSeries(actualTaskSequence));
		List<Long> expectedOffset = new ArrayList<>();
		expectedOffset.add(0L);
		expectedOffset.add(2000L);
		expectedOffset.add(2000L);
		expectedOffset.add(2000L);
		expectedOffset.add(2000L);
		assertTimeCloseToOffset(
				expectedBaseTime,
				getRealTimeSeries(actualTaskSequence),
				expectedOffset,
				ERROR_THRESHOLD
			);
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
		Instant expected = T("2016-08-31T00:00:01Z");
		Instant actual = scheduler.getCurrentTime();
		assertTimeEquals(expected, actual, 200, 120);
		
		Thread.sleep(1000);
		expected = actual.plusSeconds(1);
		actual = scheduler.getCurrentTime();
		assertTimeEquals(expected, actual, 200, 120);
		
		Thread.sleep(1000);
		expected = actual.plusSeconds(1);
		actual = scheduler.getCurrentTime();
		assertTimeEquals(expected, actual, 200, 120);
	}
	
	@Test
	public void testRunningWithoutTasks_Speed2() throws Exception {
		scheduler.setExecutionSpeed(2);
		scheduler.setModeRun();
		
		Thread.sleep(1000);
		Instant expected = T("2016-08-31T00:00:02Z");
		Instant actual = scheduler.getCurrentTime();
		// Simulator is slower than real-time due to overhead. With high
		// probability the actual time will be less than expected. Use the
		// greater error as lower allowed error.
		assertTimeEquals(expected, actual, 500, 120); 
		
		Thread.sleep(1000);
		expected = actual.plusSeconds(2);
		actual = scheduler.getCurrentTime();
		assertTimeEquals(expected, actual, 500, 120);
		
		Thread.sleep(1000);
		expected = actual.plusSeconds(2);
		actual = scheduler.getCurrentTime();
		assertTimeEquals(expected, actual, 500, 120);
	}

}
