package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.SchedulerTestComponents.*;

public class SchedulerLocal_PoolTest {
	private Runnable task1, task2, task3;
	private Scheduler scheduler;
	private SchedulerLocal_TimerTask tt1, tt2, tt3;
	private SchedulerLocal_Pool pool;

	@Before
	public void setUp() throws Exception {
		scheduler = new MyScheduler();
		task1 = new MyRunnable();
		task2 = new MyRunnable();
		task3 = new MyRunnable();
		tt1 = new SchedulerLocal_TimerTask(task1, scheduler);
		tt2 = new SchedulerLocal_TimerTask(task2, scheduler);
		tt3 = new SchedulerLocal_TimerTask(task3, scheduler);
		pool = new SchedulerLocal_Pool();
	}
	
	@Test
	public void testPutAndGet() throws Exception {
		assertSame(tt1, pool.put(tt1));
		assertSame(tt2, pool.put(tt2));
		assertSame(tt1, pool.get(task1));
		assertSame(tt2, pool.get(task2));
		assertNull(pool.get(task3));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testPut_ThrowsIfAlreadyExists() throws Exception {
		pool.put(tt1);
		pool.put(tt1);
	}
	
	@Test
	public void testExists() throws Exception {
		assertFalse(pool.exists(task1));
		assertFalse(pool.exists(task2));
		assertFalse(pool.exists(task3));
		pool.put(tt1);
		pool.put(tt3);
		assertTrue(pool.exists(task1));
		assertFalse(pool.exists(task2));
		assertTrue(pool.exists(task3));
	}
	
	@Test
	public void testRemove() throws Exception {
		pool.put(tt1);
		pool.put(tt2);
		pool.put(tt3);
		pool.remove(task1);
		pool.remove(task3);
		assertFalse(pool.exists(task1));
		assertTrue(pool.exists(task2));
		assertFalse(pool.exists(task3));
	}

}
