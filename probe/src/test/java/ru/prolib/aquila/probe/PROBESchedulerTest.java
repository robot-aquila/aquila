package ru.prolib.aquila.probe;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.probe.PROBEScheduler.Mode;
import ru.prolib.aquila.probe.PROBEScheduler.State;
import ru.prolib.aquila.probe.PROBEScheduler.Task;
import ru.prolib.aquila.probe.PROBEScheduler.TaskState;

public class PROBESchedulerTest {
	private static final long MAX_TIME_ERROR_MS = 20;	
	private PROBEScheduler scheduler;
	private List<TestTaskResult> results = new Vector<TestTaskResult>();
	@SuppressWarnings("unused")
	private TestTask task1, task2, task3, task4, task5, task6;
	@SuppressWarnings("unused")
	private TestTaskR task_r1, task_r2, task_r3, task_r4, task_r5, task_r6;
	private CountDownLatch signal;
	private volatile int taskID = 0;
	
	static class TestTaskResult {
		private final Instant timeAtExec;
		private final Object subject;
		
		public TestTaskResult(Instant timeAtExec, Object subject) {
			super();
			this.timeAtExec = timeAtExec;
			this.subject = subject;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != TestTaskResult.class ) {
				return false;
			}
			TestTaskResult o = (TestTaskResult) other;
			return new EqualsBuilder()
				.append(timeAtExec, o.timeAtExec)
				.append(subject, o.subject)
				.isEquals();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + subject + "@" + timeAtExec + "]";
		}
		
	}
	
	/**
	 * Simple one-time task.
	 */
	class TestTask implements Runnable {
		private final int id;
		
		public TestTask() {
			super();
			id = ++taskID;
		}

		@Override
		public void run() {
			results.add(new TestTaskResult(scheduler.getCurrentTime(), this));
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + id + "]";
		}
		
	}
	
	/**
	 * Repeating task.
	 */
	class TestTaskR implements Runnable {
		private final int id;
		private int execCount;
		private TaskHandler handler;
		
		public TestTaskR(int execCount) {
			super();
			id = ++taskID;
			this.execCount = execCount;
		}
		
		public TestTaskR setHandler(TaskHandler handler) {
			this.handler = handler;
			return this;
		}
		
		@Override
		public void run() {
			results.add(new TestTaskResult(scheduler.getCurrentTime(), this));
			execCount --;
			if ( execCount == 0 ) {
				handler.cancel();
				handler = null;
			}
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + id + "]";
		}
		
	}
	
	static class WaitForStateChange implements Observer {
		private final CountDownLatch signal;
		private final State expectedState;
		
		public WaitForStateChange(State expectedState, CountDownLatch signal) {
			this.signal = signal;
			this.expectedState = expectedState;
		}

		@Override
		public void update(Observable object, Object subject) {
			PROBEScheduler dummy = (PROBEScheduler) object;
			if ( dummy.getState() == expectedState ) {
				signal.countDown();
			}
		}
		
	}

	@Before
	public void setUp() throws Exception {
		scheduler = new PROBEScheduler();
		signal = new CountDownLatch(1);
	}
	
	@After
	public void tearDown() throws Exception {
		scheduler.close();
		results.clear();
		task1 = task2 = task3 = task4 = task5 = task6 = null;
		task_r1 = task_r2 = task_r3 = task_r4 = task_r5 = task_r6 = null;
		taskID = 0;
	}
	
	/**
	 * Shortcut for instantiate timestamps.
	 * <p>
	 * @param timestr - time string
	 * @return timestamp
	 */
	private static Instant T(String timestr) {
		return Instant.parse(timestr);
	}
	
	private static TestTaskResult result(String timestr, Object subject) {
		return new TestTaskResult(T(timestr), subject);
	}
	
	private static void assertTimeError(long expected, long actual) {
		long err = Math.abs(expected - actual);
		assertTrue("Expected time error <= " + MAX_TIME_ERROR_MS
				+ " but: " + err, err <= MAX_TIME_ERROR_MS);
	}
	
	@Test
	public void testCtor() throws Exception {
		assertEquals(Mode.WAIT, scheduler.getMode());
		assertEquals(State.WAIT_FOR_MODE, scheduler.getState());
		assertFalse(scheduler.getRealtimeDelays());
		assertEquals(0, scheduler.getCurrentTime().toEpochMilli());
	}
	
	@Test
	public void testSchedule_AllScheduleMethods() throws Exception {
		//  1 time: T+1000
		task1 = new TestTask();
		Task h1 = (Task)scheduler.schedule(task1, T("2015-12-31T00:00:01Z"));
		// 3 times: T, T+500, T+1000
		task_r2 = new TestTaskR(3);
		Task h2 = (Task)scheduler.schedule(task_r2, T("2015-12-31T00:00:00Z"), 500);
		task_r2.setHandler(h2);
		// Epoch T+1000
		task3 = new TestTask();
		Task h3 = (Task)scheduler.schedule(task3, 1000);
		// 2 times: Epoch T+1200, +1300
		task_r4 = new TestTaskR(2);
		Task h4 = (Task)scheduler.schedule(task_r4, 1200, 100);
		task_r4.setHandler(h4);
		// 5 times: T+500, +750, +1000, +1250, +1500
		task_r5 = new TestTaskR(5);
		Task h5 = (Task)scheduler.scheduleAtFixedRate(task_r5, T("2015-12-31T00:00:00.500Z"), 250);
		task_r5.setHandler(h5);
		// 2 times: Epoch T+800, +950
		task_r6 = new TestTaskR(2);
		Task h6 = (Task)scheduler.scheduleAtFixedRate(task_r6, 800, 150);
		task_r6.setHandler(h6);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(500, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.801Z", task_r6));
		expected.add(result("1970-01-01T00:00:00.951Z", task_r6));
		expected.add(result("1970-01-01T00:00:01.001Z", task3));
		expected.add(result("1970-01-01T00:00:01.201Z", task_r4));
		expected.add(result("1970-01-01T00:00:01.301Z", task_r4));
		expected.add(result("2015-12-31T00:00:00.001Z", task_r2));
		expected.add(result("2015-12-31T00:00:00.501Z", task_r5));
		expected.add(result("2015-12-31T00:00:00.501Z", task_r2));
		expected.add(result("2015-12-31T00:00:00.751Z", task_r5));
		expected.add(result("2015-12-31T00:00:01.001Z", task1));
		expected.add(result("2015-12-31T00:00:01.001Z", task_r2));
		expected.add(result("2015-12-31T00:00:01.001Z", task_r5));
		expected.add(result("2015-12-31T00:00:01.251Z", task_r5));
		expected.add(result("2015-12-31T00:00:01.501Z", task_r5));
		assertEquals(expected, results);
		assertTrue(h1.isState(TaskState.EXECUTED));
		assertTrue(h2.isState(TaskState.CANCELLED));
		assertTrue(h3.isState(TaskState.EXECUTED));
		assertTrue(h4.isState(TaskState.CANCELLED));
		assertTrue(h5.isState(TaskState.CANCELLED));
		assertTrue(h6.isState(TaskState.CANCELLED));
	}
	
	@Test
	public void testSchedule_PullAndRunWithRealtimeDelays() throws Exception {
		task1 = new TestTask();
		Task h1 = (Task)scheduler.schedule(task1, T("1970-01-01T00:00:00.050Z"));
		task2 = new TestTask();
		Task h2 = (Task)scheduler.schedule(task2, T("1970-01-01T00:00:00.150Z"));
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		scheduler.setRealtimeDelays(true);
		long expectedDelay = 150;
		long startTime = System.currentTimeMillis();
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		long actualDelay = System.currentTimeMillis() - startTime;
		assertTimeError(expectedDelay, actualDelay);
		assertEquals(151, scheduler.getCurrentTimestamp());
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.051Z", task1));
		expected.add(result("1970-01-01T00:00:00.151Z", task2));
		assertEquals(expected, results);
		assertTrue(h1.isState(TaskState.EXECUTED));
		assertTrue(h2.isState(TaskState.EXECUTED));
	}
	
	@Test
	public void testSchedule_PullAndRunWithRealtimeDelays_WithNewTask()
			throws Exception
	{
		task1 = new TestTask();
		Task h1 = (Task)scheduler.schedule(task1, 150);
		task2 = new TestTask();
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		scheduler.setRealtimeDelays(true);
		long expectedDelay = 150;
		long startTime = System.currentTimeMillis();
		
		scheduler.switchToPullAndRun();
		Thread.sleep(50);
		Task h2 = (Task)scheduler.schedule(task2, 90);
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		long actualDelay = System.currentTimeMillis() - startTime;
		assertTimeError(expectedDelay, actualDelay);
		assertEquals(151, scheduler.getCurrentTimestamp());
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.091Z", task2));
		expected.add(result("1970-01-01T00:00:00.151Z", task1));
		assertEquals(expected, results);
		assertTrue(h1.isState(TaskState.EXECUTED));
		assertTrue(h2.isState(TaskState.EXECUTED));
	}
	
	@Test
	public void testSchedule_PullAndRunWithRealtimeDelays_WithSwitchOffRealtimeDelays()
			throws Exception
	{
		task1 = new TestTask();
		Task h = (Task)scheduler.schedule(task1, 200);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		scheduler.setRealtimeDelays(true);
		long expectedDelay = 100;
		long startTime = System.currentTimeMillis();
		
		scheduler.switchToPullAndRun();
		Thread.sleep(100);
		scheduler.setRealtimeDelays(false);
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		long actualDelay = System.currentTimeMillis() - startTime;
		assertTimeError(expectedDelay, actualDelay);
		assertEquals(201, scheduler.getCurrentTimestamp());
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.201Z", task1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.EXECUTED));
	}

	@Test
	public void testSchedule_PullAndRunWithRealtimeDelays_WithModeSwitch()
			throws Exception
	{
		task1 = new TestTask();
		Task h = (Task)scheduler.schedule(task1, 200);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_MODE, signal));
		scheduler.setRealtimeDelays(true);
		long expectedDelay = 120;
		long startTime = System.currentTimeMillis();
		
		scheduler.switchToPullAndRun();
		Thread.sleep(120);
		scheduler.switchToWait();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		long actualDelay = System.currentTimeMillis() - startTime;
		assertTimeError(expectedDelay, actualDelay);
		assertTimeError(expectedDelay, scheduler.getCurrentTimestamp());
		assertNotEquals(121, scheduler.getCurrentTimestamp());
		assertEquals(0, results.size());
		assertTrue(h.isState(TaskState.SCHEDULED));
	}
	
	@Test
	public void testSchedule_RI_ThrowsTaskInThePast() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.schedule(task1, T("1969-12-31T23:50:00Z"));
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] in the past 1969-12-31T23:50:00Z. "
				+ "Current time is 1970-01-01T00:00:00Z", e.getMessage());
		}
	}
	
	@Test
	public void testSchedule_RI_TaskAtTheMomentIsOK() throws Exception {
		task1 = new TestTask();
		Task h = (Task) scheduler.schedule(task1, T("1970-01-01T00:00:00Z"));
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.001Z", task1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.EXECUTED));
	}

	@Test
	public void testSchedule_RIL_ThrowsTaskInThePast() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.schedule(task1, T("1969-12-31T23:59:59.950Z"), 100);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] in the past 1969-12-31T23:59:59.950Z. "
				+ "Current time is 1970-01-01T00:00:00Z", e.getMessage());
		}
	}

	@Test
	public void testSchedule_RIL_TaskAtTheMomentIsOK()
			throws Exception
	{
		task_r1 = new TestTaskR(2);
		Task h = (Task) scheduler.schedule(task_r1, T("1970-01-01T00:00:00Z"), 20);
		task_r1.setHandler(h);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.001Z", task_r1));
		expected.add(result("1970-01-01T00:00:00.021Z", task_r1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.CANCELLED));
	}

	@Test
	public void testSchedule_RIL_ThrowsNegativePeriod() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.schedule(task1, T("1970-01-01T00:00:01Z"), -200);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] period must be positive but: -200", e.getMessage());
		}
	}
	
	@Test
	public void testSchedule_RL_ThrowsNegativeDelay() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.schedule(task1, -500);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] delay must be positive but: -500", e.getMessage());
		}
	}
	
	@Test
	public void testSchedule_RL_ZeroDelayIsOK() throws Exception {
		task1 = new TestTask();
		Task h = (Task) scheduler.schedule(task1, 0);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.001Z", task1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.EXECUTED));	}

	@Test
	public void testSchedule_RLL_ThrowsNegativeDelay() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.schedule(task1, -800, 300);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] delay must be positive but: -800", e.getMessage());
		}
	}

	@Test
	public void testSchedule_RLL_ZeroDelayIsOK() throws Exception {
		task_r1 = new TestTaskR(3);
		Task h = (Task) scheduler.schedule(task_r1, 0, 100);
		task_r1.setHandler(h);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.001Z", task_r1));
		expected.add(result("1970-01-01T00:00:00.101Z", task_r1));
		expected.add(result("1970-01-01T00:00:00.201Z", task_r1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.CANCELLED));
	}

	@Test
	public void testSchedule_RLL_ThrowsNegativePeriod() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.schedule(task1, 100, -108);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] period must be positive but: -108", e.getMessage());
		}
	}
	
	@Test
	public void testScheduleAtFixedRate_RIL_ThrowsTaskInThePast() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.scheduleAtFixedRate(task1, T("1969-12-31T23:59:59.950Z"), 100);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] in the past 1969-12-31T23:59:59.950Z. "
				+ "Current time is 1970-01-01T00:00:00Z", e.getMessage());
		}
	}
	
	@Test
	public void testScheduleAtFixedRate_RIL_TaskAtTheMomentIsOK() throws Exception {
		task_r1 = new TestTaskR(2);
		Task h = (Task) scheduler.scheduleAtFixedRate(task_r1, T("1970-01-01T00:00:00Z"), 40);
		task_r1.setHandler(h);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.001Z", task_r1));
		expected.add(result("1970-01-01T00:00:00.041Z", task_r1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.CANCELLED));
	}
	
	@Test
	public void testScheduleAtFixedRate_RIL_ThrowsNegativePeriod() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.scheduleAtFixedRate(task1, T("1970-01-01T00:00:20Z"), -500);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] period must be positive but: -500", e.getMessage());
		}
	}

	@Test
	public void testScheduleAtFixedRate_RLL_ThrowsNegativeDelay() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.scheduleAtFixedRate(task1, -1000, 500);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] delay must be positive but: -1000", e.getMessage());
		}
	}
	
	@Test
	public void testScheduleAtFixedRate_RLL_TaskAtTheMomentIsOK() throws Exception {
		task_r1 = new TestTaskR(3);
		Task h = (Task) scheduler.scheduleAtFixedRate(task_r1, 0, 50);
		task_r1.setHandler(h);
		scheduler.addObserver(new WaitForStateChange(State.WAIT_FOR_TASK, signal));
		
		scheduler.switchToPullAndRun();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		Vector<TestTaskResult> expected = new Vector<TestTaskResult>();
		expected.add(result("1970-01-01T00:00:00.001Z", task_r1));
		expected.add(result("1970-01-01T00:00:00.051Z", task_r1));
		expected.add(result("1970-01-01T00:00:00.101Z", task_r1));
		assertEquals(expected, results);
		assertTrue(h.isState(TaskState.CANCELLED));
	}
	
	@Test
	public void testScheduleAtFixedRate_RLL_ThrowsNegativePeriod() throws Exception {
		task1 = new TestTask();
		try {
			scheduler.scheduleAtFixedRate(task1, 1000, -2500);
			fail("Expected exception: " + IllegalArgumentException.class);
		} catch ( IllegalArgumentException e ) {
			assertEquals("Task TestTask[1] period must be positive but: -2500", e.getMessage());
		}
	}

	@Test
	public void testClose_AllListenersRemovedAfterStateChangedNotification()
		throws Exception
	{
		scheduler.addObserver(new WaitForStateChange(State.CLOSED, signal));
		
		scheduler.close();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		assertEquals(0, scheduler.countObservers());
	}
	
	@Test
	public void testClose_CancelAllTasks() throws Exception {
		Task h1 = (Task) scheduler.schedule(new TestTask(), 200);
		Task h2 = (Task) scheduler.schedule(new TestTask(), 200);
		Task h3 = (Task) scheduler.schedule(new TestTask(), 500);
		Task h4 = (Task) scheduler.schedule(new TestTask(), 100);
		Task h5 = (Task) scheduler.schedule(new TestTask(), 500);
		Task h6 = (Task) scheduler.schedule(new TestTask(), 900);
		scheduler.addObserver(new WaitForStateChange(State.CLOSED, signal));
		
		scheduler.close();
		
		assertTrue(signal.await(1000, TimeUnit.MILLISECONDS));
		assertTrue(h1.isState(TaskState.CANCELLED));
		assertTrue(h2.isState(TaskState.CANCELLED));
		assertTrue(h3.isState(TaskState.CANCELLED));
		assertTrue(h4.isState(TaskState.CANCELLED));
		assertTrue(h5.isState(TaskState.CANCELLED));
		assertTrue(h6.isState(TaskState.CANCELLED));
	}
	
	@Test
	public void testSwitchToPullAndRun_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		try {
			scheduler.switchToPullAndRun();
			fail("Expected exception: " + IllegalStateException.class);
		} catch ( IllegalStateException e ) {
			assertEquals("Object is closed", e.getMessage());
		}
	}

	@Test (expected=IllegalStateException.class)
	public void testSwitchToWait_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		scheduler.switchToWait();;
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RI_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		scheduler.schedule(new TestTask(), T("2015-01-01T00:00:00Z"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RIL_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		scheduler.schedule(new TestTask(), T("2015-01-01T00:00:00Z"), 100);
	}
	
	@Test
	public void testSchedule_RL_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		try {
			scheduler.schedule(new TestTask(), 1000);
			fail("Expected exception: " + IllegalStateException.class);
		} catch ( IllegalStateException e ) {
			assertEquals("Object is closed", e.getMessage());
		}
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RLL_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		scheduler.schedule(new TestTask(), 1000, 1000);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleAtFixedRate_RIL_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		scheduler.scheduleAtFixedRate(new TestTask(), T("2015-01-01T00:00:00Z"), 10);
	}

	@Test (expected=IllegalStateException.class)
	public void testScheduleAtFixedRate_RLL_ThrowsAfterClosing() throws Exception {
		scheduler.close();
		
		scheduler.scheduleAtFixedRate(new TestTask(), 500, 1000);
	}

	// TODO: check when it emulates delay and actual delay is more than expected

}
