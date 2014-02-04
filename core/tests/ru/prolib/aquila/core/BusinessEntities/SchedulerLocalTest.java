package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Timer;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class SchedulerLocalTest {
	private IMocksControl control;
	private SchedulerLocal scheduler;
	private Runnable task;
	private DateTime time = new DateTime(2013, 10, 9, 14, 12, 47);
	private Timer timer;
	private SchedulerLocal_Pool pool;
	private SchedulerLocal_TimerTask tt;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timer = control.createMock(Timer.class);
		task = control.createMock(Runnable.class);
		pool = control.createMock(SchedulerLocal_Pool.class);
		scheduler = new SchedulerLocal(timer, pool);
		tt = new SchedulerLocal_TimerTask(task, scheduler);
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		assertEquals(new DateTime(), scheduler.getCurrentTime());
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
		timer.schedule(eq(tt), eq(time.toDate()));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, time);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TDL() throws Exception {
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.schedule(eq(tt), eq(time.toDate()), eq(215L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, time, 215L);
		
		control.verify();
		assertEquals(expected, actual);
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
	public void testSchedule_TLL() throws Exception {
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.schedule(eq(tt), eq(118L), eq(215L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.schedule(task, 118L, 215L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL() throws Exception {
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.scheduleAtFixedRate(eq(tt), eq(time.toDate()), eq(302L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.scheduleAtFixedRate(task, time, 302L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL() throws Exception {
		expect(pool.put(eq(tt))).andReturn(tt);
		timer.scheduleAtFixedRate(eq(tt), eq(80L), eq(94L));
		control.replay();
		
		TaskHandler expected = new TaskHandlerImpl(task, scheduler),
			actual = scheduler.scheduleAtFixedRate(task, 80L, 94L);
		
		control.verify();
		assertEquals(expected, actual);
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

}
