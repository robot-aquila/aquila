package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

public class SchedulerLocal_TimerTaskTest {
	private IMocksControl control;
	private Runnable runnable1, runnable2;

	private SchedulerLocal_TimerTask timerTask1, timerTask2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		runnable1 = control.createMock(Runnable.class);
		runnable2 = control.createMock(Runnable.class);
		timerTask1 = new SchedulerLocal_TimerTask(runnable1, true);
		timerTask2 = new SchedulerLocal_TimerTask(runnable2, false);
	}
	
	@Test
	public void testRun_RunOnce() throws Exception {
		runnable1.run();
		control.replay();
		
		timerTask1.run();
		timerTask1.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_RunRepeatedly() throws Exception {
		runnable2.run();
		runnable2.run();
		runnable2.run();
		control.replay();
		
		timerTask2.run();
		timerTask2.run();
		timerTask2.run();
		
		control.verify();	
	}
	
	@Test
	public void testRun_CatchUnhandledException() throws Exception {
		runnable2.run();
		expectLastCall().andThrow(new NullPointerException("Critical error must not stop the scheduler"));
		control.replay();
		
		timerTask2.run();
		
		control.verify();
	}
	
	@Test
	public void testCancel() throws Exception {
		control.replay();
		timerTask1.cancel();
		
		timerTask1.run();
		
		control.verify();		
	}
	
	@Test
	public void testEquals_ScpecialCases() throws Exception {
		assertTrue(timerTask1.equals(timerTask1));
		assertFalse(timerTask1.equals(null));
		assertFalse(timerTask1.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertEquals(timerTask1, new SchedulerLocal_TimerTask(runnable1, true));
		assertNotEquals(timerTask1, timerTask2);
	}
	
	@Test
	public void testCancelWhenRunningGetStuckBugfix() throws Exception {
		final CountDownLatch started = new CountDownLatch(1);
		final CountDownLatch cancelled = new CountDownLatch(1);
		final CountDownLatch finished = new CountDownLatch(1);
		final Runnable blocker = new Runnable() {
			@Override public void run() {
				started.countDown();
				try {
					if ( cancelled.await(500, TimeUnit.MILLISECONDS) ) {
						finished.countDown();
					}
				} catch ( InterruptedException e ) {
					return;
				}
			}
		};
		SchedulerLocal_TimerTask dummyTask = new SchedulerLocal_TimerTask(blocker, false);
		Timer timer = new Timer(true);
		timer.schedule(dummyTask, 50L);
		assertTrue(started.await(500L, TimeUnit.MILLISECONDS));
		dummyTask.cancel();
		cancelled.countDown();
		assertTrue(finished.await(500L, TimeUnit.MILLISECONDS));
	}

}
