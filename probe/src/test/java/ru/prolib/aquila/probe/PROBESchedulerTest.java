package ru.prolib.aquila.probe;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.TaskHandler;

public class PROBESchedulerTest {
	private PROBEScheduler scheduler;
	private List<TestTaskResult> results = new Vector<TestTaskResult>();
	private TestTask task1, task2, task3, task4, task5, task6;
	private TestTaskR task_r1, task_r2, task_r3, task_r4, task_r5, task_r6;
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

	@Before
	public void setUp() throws Exception {
		scheduler = new PROBEScheduler();
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
	
	@Test
	public void testSchedule_AllScheduleMethods() throws Exception {
		//  1 time: T+1000
		task1 = new TestTask();
		scheduler.schedule(task1, T("2015-12-31T00:00:01Z"));
		// 3 times: T, T+500, T+1000
		task_r2 = new TestTaskR(3);
		task_r2.setHandler(scheduler.schedule(task_r2, T("2015-12-31T00:00:00Z"), 500));
		// Epoch T+1000
		task3 = new TestTask();
		scheduler.schedule(task3, 1000);
		// 2 times: Epoch T+1200, +1300
		task_r4 = new TestTaskR(2);
		task_r4.setHandler(scheduler.schedule(task_r4, 1200, 100));
		// 5 times: T+500, +750, +1000, +1250, +1500
		task_r5 = new TestTaskR(5);
		task_r5.setHandler(scheduler.scheduleAtFixedRate(task_r5, T("2015-12-31T00:00:00.500Z"), 250));
		// 2 times: Epoch T+800, +950
		task_r6 = new TestTaskR(2);
		task_r6.setHandler(scheduler.scheduleAtFixedRate(task_r6, 800, 150));

		scheduler.switchToPullAndRun();
		Thread.sleep(1000);
		
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
	}

}
