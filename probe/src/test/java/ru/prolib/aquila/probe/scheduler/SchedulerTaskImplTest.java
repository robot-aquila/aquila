package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchedulerTaskImplTest {
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private Runnable runnable1, runnable2;
	private SchedulerTaskImpl task;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		runnable1 = control.createMock(Runnable.class);
		runnable2 = new Runnable() {
			@Override public String toString() { return "foobar"; }
			@Override public void run() { }
		};
		task = new SchedulerTaskImpl(runnable1, 5000L);
	}

	@Test
	public void testCtor2() {
		assertNotNull(task.getLID());
		assertEquals(SchedulerTaskState.PENDING, task.getState());
		assertTrue(task.isPeriodic());
		assertSame(runnable1, task.getRunnable());
		assertEquals(5000L, task.getPeriod());
		assertNull(task.getNextExecutionTime());
	}
	
	@Test
	public void testCtor1() {
		task = new SchedulerTaskImpl(runnable1);
		assertEquals(SchedulerTaskState.PENDING, task.getState());
		assertFalse(task.isPeriodic());
		assertSame(runnable1, task.getRunnable());
		assertEquals(0L, task.getPeriod());
		assertNull(task.getNextExecutionTime());
	}
	
	@Test
	public void testScheduleForFirstExecution() {
		Instant actual = task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		
		Instant expected = Instant.parse("1970-01-01T00:00:01Z");
		assertEquals(expected, actual);
		assertEquals(expected, task.getNextExecutionTime());
		assertTrue(task.stateEqualsTo(SchedulerTaskState.SCHEDULED));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForFirstExecution_ThrowsIfCancelled() {
		task.cancel();
		
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForFirstExecution_ThrowsIfError() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		expectLastCall().andThrow(new RuntimeException("Test error"));
		control.replay();
		task.execute();
		
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForFirstExecution_ThrowsIfScheduled() {
		task.scheduleForFirstExecution(Instant.EPOCH, 0);
		
		task.scheduleForFirstExecution(Instant.EPOCH, 0);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForFirstExecution_ThrowsIfExecuted() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		control.replay();
		task.execute();
		
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
	}
	
	@Test
	public void testCancel_OkIfPending() {
		assertTrue(task.cancel());
		
		assertTrue(task.stateEqualsTo(SchedulerTaskState.CANCELLED));
	}
	
	@Test
	public void testCancel_OkIfScheduled() {
		task.scheduleForFirstExecution(Instant.EPOCH, 2000L);
		
		assertTrue(task.cancel());
		
		assertTrue(task.stateEqualsTo(SchedulerTaskState.CANCELLED));
	}
	
	@Test
	public void testCancel_SkipIfCancelled() {
		task.cancel();
		
		assertFalse(task.cancel());
	}
	
	@Test
	public void testCancel_SkipIfError() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		expectLastCall().andThrow(new RuntimeException("Test error"));
		control.replay();
		task.execute();
		
		assertFalse(task.cancel());
		
		assertTrue(task.stateEqualsTo(SchedulerTaskState.ERROR));
	}
	
	@Test
	public void testCancel_SkipIfExecuted() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		control.replay();
		task.execute();
		
		assertFalse(task.cancel());
		
		assertTrue(task.stateEqualsTo(SchedulerTaskState.EXECUTED));
	}
	
	@Test
	public void testExecute() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 0L);
		runnable1.run();
		control.replay();
		
		task.execute();
		
		assertTrue(task.stateEqualsTo(SchedulerTaskState.EXECUTED));
	}
	
	@Test
	public void testExecute_Periodic() {
		task.scheduleForFirstExecution(Instant.EPOCH, 0L);
		runnable1.run();
		control.replay();
		
		task.execute();
		
		assertTrue(task.stateEqualsTo(SchedulerTaskState.SCHEDULED));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testExecute_ThrowsIfPending() {
		task.execute();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testExecute_ThrowsIfError() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		expectLastCall().andThrow(new RuntimeException("Test error"));
		control.replay();
		task.execute();

		task.execute();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testExecute_ThrowsIfExecuted() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		control.replay();
		task.execute();

		task.execute();
	}
	
	@Test
	public void testExecute_SkipIfCancelled() {
		task.cancel();
		control.replay();
		
		task.execute();
		
		control.verify();
	}

	@Test
	public void testScheduleForNextExecution_OkIfScheduled() {
		task.scheduleForFirstExecution(Instant.EPOCH, 5000L);
		
		Instant actual = task.scheduleForNextExecution(Instant.EPOCH.plusSeconds(10));
		
		Instant expected = Instant.parse("1970-01-01T00:00:15Z");
		assertEquals(expected, actual);
		assertEquals(expected, task.getNextExecutionTime());
		assertTrue(task.stateEqualsTo(SchedulerTaskState.SCHEDULED));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForNextExecution_ThrowsIfNotPeriodic() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		
		task.scheduleForNextExecution(Instant.EPOCH.plusSeconds(10));
	}

	@Test (expected=IllegalStateException.class)
	public void testScheduleForNextExecution_ThrowsIfPending() {
		task.scheduleForNextExecution(Instant.EPOCH.plusSeconds(10));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForNextExecution_ThrowsIfError() {
		task = new SchedulerTaskImpl(runnable1);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		expectLastCall().andThrow(new RuntimeException("Test error"));
		control.replay();
		task.execute();

		task.scheduleForNextExecution(Instant.EPOCH.plusSeconds(10));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleForNextExecution_ThrowsIfCancelled() {
		task.cancel();
		
		task.scheduleForNextExecution(Instant.EPOCH.plusSeconds(10));
	}
	
	@Test
	public void testToString() {
		task = new SchedulerTaskImpl(runnable2);
		
		assertEquals("SchedulerTaskImpl[PENDING foobar]", task.toString());
	}
	
	@Test
	public void testToString_Scheduled() {
		task = new SchedulerTaskImpl(runnable2, 1500L);
		task.scheduleForFirstExecution(Instant.parse("2016-08-25T21:57:41Z"), 10000L);
		
		assertEquals("SchedulerTaskImpl[2016-08-25T21:57:51Z P:1500 foobar]", task.toString());
	}
	
	@Test
	public void testToString_Periodic() {
		task = new SchedulerTaskImpl(runnable2, 1500L);
		
		assertEquals("SchedulerTaskImpl[PENDING P:1500 foobar]", task.toString());
	}
	
	@Test
	public void testToString_Error() {
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		expectLastCall().andThrow(new RuntimeException("Test error"));
		control.replay();
		task.execute();
		
		assertEquals("SchedulerTaskImpl[ERROR P:5000 EasyMock for interface java.lang.Runnable]", task.toString());
	}
	
	@Test
	public void testToString_Executed() {
		task = new SchedulerTaskImpl(runnable2);
		task.scheduleForFirstExecution(Instant.EPOCH, 1000L);
		runnable1.run();
		control.replay();
		task.execute();
		
		assertEquals("SchedulerTaskImpl[EXECUTED foobar]", task.toString());
	}
	
	@Test
	public void testToString_Cancelled() {
		task = new SchedulerTaskImpl(runnable2);
		task.cancel();
		
		assertEquals("SchedulerTaskImpl[CANCELLED foobar]", task.toString());
	}

	@Test
	public void testDeadlockTest_CombinationExecuteWithCancel() throws Exception {
		// Two threads: one is execute task, the second wants to cancel task exactly at same time.
		// The case: if the second thread locks shared object prior to cancel task and the task is
		// executed it causes deadlock. The first thread wont release task object because waits
		// for shared object. The second thread wont release lock on shared object because waits
		// for task object.
		// How to test: the second thread should enter and lock shared object, then wait until the
		// task start execution inside the first thread. After that the second thread should proceed
		// to cancel of task.
		Lock shared_lock = new ReentrantLock();
		CountDownLatch first_started = new CountDownLatch(1);
		CountDownLatch second_started = new CountDownLatch(1);
		CountDownLatch finished = new CountDownLatch(2);
		task = new SchedulerTaskImpl(new Runnable() {
			@Override
			public void run() {
				// Will be called in the first thread.
				first_started.countDown();
				try {
					if ( ! second_started.await(500L, TimeUnit.MILLISECONDS) ) {
						return;
					}
					if ( shared_lock.tryLock(100L, TimeUnit.MILLISECONDS) ) {
						shared_lock.unlock();
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
		});
		task.scheduleForFirstExecution(Instant.parse("2019-03-05T16:19:00Z"));
		Thread first_thread = new Thread() {
			@Override
			public void run() {
				task.execute();
			}
		};
		Thread second_thread = new Thread() {
			@Override
			public void run() {
				shared_lock.lock();
				try {
					second_started.countDown();
					if ( ! first_started.await(500L, TimeUnit.MILLISECONDS) ) {
						return;
					}
					task.cancel();
					finished.countDown();
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				} finally {
					shared_lock.unlock();
				}
			}
		};
		first_thread.start();
		second_thread.start();
		
		if ( ! finished.await(1L, TimeUnit.SECONDS) ) {
			first_thread.interrupt();
			second_thread.interrupt();
			fail("Deadlock detected");
		}
	}

}
