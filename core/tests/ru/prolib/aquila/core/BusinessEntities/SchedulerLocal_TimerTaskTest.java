package ru.prolib.aquila.core.BusinessEntities;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.core.BusinessEntities.SchedulerTestComponents.*;

public class SchedulerLocal_TimerTaskTest {
	private IMocksControl control;
	private Runnable task;
	private Scheduler scheduler;
	private SchedulerLocal_TimerTask timerTask1, timerTask2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		task = control.createMock(Runnable.class);
		scheduler = control.createMock(Scheduler.class);
		timerTask1 = new SchedulerLocal_TimerTask(task, scheduler);
		timerTask2 = new SchedulerLocal_TimerTask(task);
	}
	
	@Test
	public void testRun_AutoClear() throws Exception {
		scheduler.cancel(same(task));
		task.run();
		control.replay();
		
		timerTask1.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_WithoutAutoClear() throws Exception {
		task.run();
		control.replay();
		
		timerTask2.run();
		
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
		task = new MyRunnable();
		scheduler = new MyScheduler();
		timerTask1 = new SchedulerLocal_TimerTask(task, scheduler);
		Variant<Runnable> vTask = new Variant<Runnable>()
			.add(task)
			.add(new MyRunnable());
		Variant<Scheduler> vSchd = new Variant<Scheduler>(vTask)
			.add(scheduler)
			.add(new MyScheduler());
		Variant<?> iterator = vSchd;
		int foundCnt = 0;
		SchedulerLocal_TimerTask x, found = null;
		do {
			x = new SchedulerLocal_TimerTask(vTask.get(), vSchd.get());
			if ( timerTask1.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(task, found.getTask());
		assertSame(scheduler, found.getScheduler());
	}

}
