package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Timer;
import java.util.TimerTask;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class SchedulerLocalTest {
	private IMocksControl control;
	private SchedulerLocal scheduler;
	private TimerTask task;
	private DateTime time = new DateTime(2013, 10, 9, 14, 12, 47);
	private Timer timer;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timer = control.createMock(Timer.class);
		task = control.createMock(TimerTask.class);
		scheduler = new SchedulerLocal(timer);
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		assertEquals(new DateTime(), scheduler.getCurrentTime());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(scheduler.equals(scheduler));
		assertTrue(scheduler.equals(new SchedulerLocal()));
		assertFalse(scheduler.equals(null));
		assertFalse(scheduler.equals(this));
	}
	
	@Test
	public void testConstruct0() throws Exception {
		scheduler = new SchedulerLocal();
		assertNotNull(scheduler.getTimer());
	}
	
	@Test
	public void testSchedule_TD() throws Exception {
		timer.schedule(task, time.toDate());
		control.replay();
		
		scheduler.schedule(task, time);
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TDL() throws Exception {
		timer.schedule(task, time.toDate(), 215L);
		control.replay();
		
		scheduler.schedule(task, time, 215L);
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TL() throws Exception {
		timer.schedule(task, 220L);
		control.replay();
		
		scheduler.schedule(task, 220L);
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TLL() throws Exception {
		timer.schedule(task, 118L, 215L);
		control.replay();
		
		scheduler.schedule(task, 118L, 215L);
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL() throws Exception {
		timer.scheduleAtFixedRate(task, time.toDate(), 302L);
		control.replay();
		
		scheduler.scheduleAtFixedRate(task, time, 302L);
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL() throws Exception {
		timer.scheduleAtFixedRate(task, 80L, 94L);
		control.replay();
		
		scheduler.scheduleAtFixedRate(task, 80L, 94L);
		
		control.verify();
	}
	
	@Test
	public void testCancel() throws Exception {
		expect(task.cancel()).andReturn(true);
		control.replay();
		
		assertTrue(scheduler.cancel(task));
		
		control.verify();
	}

}
