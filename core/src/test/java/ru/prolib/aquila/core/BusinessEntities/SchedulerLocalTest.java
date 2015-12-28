package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SchedulerLocalTest {
	private static final int MS2NANO_MULT = 1000000;
	private IMocksControl control;
	private SchedulerLocal scheduler;
	/**
	 * The scheduler instance for integration testing.
	 */
	private SchedulerLocal scheduler_4it;
	private Runnable task;
	private LocalDateTime time;
	private Timer timer;
	private SchedulerLocal_Pool pool;
	private SchedulerLocal_TimerTask tt;
	private Calendar calendar;
	
	// Helpers for test actual execution
	private CountDownLatch finished;
	private Vector<LocalDateTime> ticks;
	private int tickLimit;
	private Runnable helper;

	@Before
	public void setUp() throws Exception {
		time = LocalDateTime.of(2013, 10, 9, 14, 12, 47, 125 * MS2NANO_MULT);
		calendar = Calendar.getInstance();
		calendar.set(2013, 9, 9, 14, 12, 47); // Don't forget month starts from 0
		calendar.set(Calendar.MILLISECOND, 125);
		control = createStrictControl();
		timer = control.createMock(Timer.class);
		task = control.createMock(Runnable.class);
		pool = control.createMock(SchedulerLocal_Pool.class);
		scheduler = new SchedulerLocal(timer, pool);
		scheduler_4it = new SchedulerLocal();
		tt = new SchedulerLocal_TimerTask(task, scheduler);
		
		finished = new CountDownLatch(1);
		ticks = new Vector<LocalDateTime>();
		tickLimit = 3;
		helper = new Runnable() {
			@Override public void run() {
				ticks.add(LocalDateTime.now());
				if ( ticks.size() >= tickLimit ) {
					scheduler_4it.cancel(this);
					finished.countDown();
				}
			}
		};
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		assertEquals(LocalDateTime.now(), scheduler.getCurrentTime());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(scheduler.equals(scheduler));
		assertFalse(scheduler.equals(new SchedulerLocal()));
		assertFalse(scheduler.equals(null));
		assertFalse(scheduler.equals(this));
	}
	
	@Test
	public void testConstruct0() throws Exception {
		scheduler = new SchedulerLocal();
		assertNotNull(scheduler.getTimer());
		assertNotNull(scheduler.getPool());
	}
	
	@Test
	public void testSchedule_TD() throws Exception {
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.schedule(eq(tt), eq(calendar.getTime()));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, time);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TD_ActualExecution() throws Exception {
		scheduler_4it.schedule(helper, LocalDateTime.now().plusNanos(100 * MS2NANO_MULT));
		finished.await(300, java.util.concurrent.TimeUnit.MILLISECONDS);
		assertEquals(1, ticks.size());
		scheduler_4it.cancel(helper);
	}
	
	@Test
	public void testSchedule_TDL() throws Exception {
		tt = new SchedulerLocal_TimerTask(task);
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.schedule(eq(tt), eq(calendar.getTime()), eq(215L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, time, 215L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TDL_ActualExecution() throws Exception {
		scheduler_4it.schedule(helper, LocalDateTime.now().plusNanos(100 * MS2NANO_MULT), 100);
		assertTrue(finished.await(2, java.util.concurrent.TimeUnit.SECONDS));
		assertEquals(3, ticks.size());
	}
	
	@Test
	public void testSchedule_TL() throws Exception {
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.schedule(eq(tt), eq(220L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, 220L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TL_ActualExecution() throws Exception {
		scheduler_4it.schedule(helper, 100);
		finished.await(300, java.util.concurrent.TimeUnit.MILLISECONDS);
		assertEquals(1, ticks.size());
		scheduler_4it.cancel(helper);
	}
	
	@Test
	public void testSchedule_TLL() throws Exception {
		tt = new SchedulerLocal_TimerTask(task);
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.schedule(eq(tt), eq(118L), eq(215L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, 118L, 215L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TLL_ActualExecution() throws Exception {
		scheduler_4it.schedule(helper, 50, 100);
		assertTrue(finished.await(2, java.util.concurrent.TimeUnit.SECONDS));
		assertEquals(3, ticks.size());
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL() throws Exception {
		tt = new SchedulerLocal_TimerTask(task);
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.scheduleAtFixedRate(eq(tt), eq(calendar.getTime()), eq(302L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.scheduleAtFixedRate(task, time, 302L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL_ActualExecution() throws Exception {
		scheduler_4it.scheduleAtFixedRate(helper, LocalDateTime.now().plusNanos(50 * MS2NANO_MULT), 100);
		assertTrue(finished.await(2, java.util.concurrent.TimeUnit.SECONDS));
		assertEquals(3, ticks.size());
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL() throws Exception {
		tt = new SchedulerLocal_TimerTask(task);
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.scheduleAtFixedRate(eq(tt), eq(80L), eq(94L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.scheduleAtFixedRate(task, 80L, 94L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL_ActualExecution() throws Exception {
		scheduler_4it.scheduleAtFixedRate(helper, 100, 100);
		assertTrue(finished.await(2, java.util.concurrent.TimeUnit.SECONDS));
		assertEquals(3, ticks.size());
	}
	
	@Test
	public void testCancel() throws Exception {
		tt = control.createMock(SchedulerLocal_TimerTask.class);
		expect(pool.exists(same(task))).andReturn(true);
		expect(pool.get(same(task))).andReturn(tt);
		expect(tt.cancel()).andReturn(false); // не имеет значения
		pool.remove(same(task));
		control.replay();
		
		scheduler.cancel(task);
		
		control.verify();
	}
	
	@Test
	public void testCancel_NonExisting() throws Exception {
		expect(pool.exists(same(task))).andReturn(false);
		control.replay();
		
		scheduler.cancel(task);
		
		control.verify();
	}
	
	@Test
	public void testScheduled() throws Exception {
		expect(pool.exists(same(task))).andReturn(true);
		expect(pool.exists(same(task))).andReturn(false);
		control.replay();
		
		assertTrue(scheduler.scheduled(task));
		assertFalse(scheduler.scheduled(task));
		
		control.verify();
	}

	@Test
	public void testGetTaskHandler() throws Exception {
		expect(pool.exists(same(task))).andReturn(true);
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.getTaskHandler(task);
		
		control.verify();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetTaskHandler_NonExisting() throws Exception {
		expect(pool.exists(same(task))).andReturn(false);
		control.replay();
		
		assertNull(scheduler.getTaskHandler(task));
		
		control.verify();
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TD_ThrowsIfTaskDuplicated() throws Exception {
		scheduler_4it.schedule(task, LocalDateTime.now().plusNanos(2000));
		scheduler_4it.schedule(task, LocalDateTime.now().plusNanos(3000));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TDL_ThrowsIfTaskDuplicated() throws Exception {
		scheduler_4it.schedule(task, LocalDateTime.now().plusNanos(800), 2000L);
		scheduler_4it.schedule(task, LocalDateTime.now().plusNanos(100), 4000L);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TL_ThrowsIfTaskDuplicated() throws Exception {
		scheduler_4it.schedule(task, 4500);
		scheduler_4it.schedule(task, 8100);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSchedule_TLL_ThrowsIfTaskDuplicated() throws Exception {
		scheduler_4it.schedule(task, 800, 5000);
		scheduler_4it.schedule(task, 740, 2900);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TDL_ThrowsIfTaskDuplicated()
			throws Exception
	{
		scheduler_4it.scheduleAtFixedRate(task, LocalDateTime.now().plusNanos(100), 800);
		scheduler_4it.scheduleAtFixedRate(task, LocalDateTime.now().plusNanos(600), 200);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testScheduleAtFixedRate_TLL_ThrowsIfTaskDuplicated()
			throws Exception
	{
		scheduler_4it.scheduleAtFixedRate(task,  4500, 1200);
		scheduler_4it.scheduleAtFixedRate(task,  8241, 1882);
	}
	
}
