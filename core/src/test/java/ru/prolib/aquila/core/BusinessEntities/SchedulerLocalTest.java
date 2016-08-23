package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchedulerLocalTest {
	private IMocksControl control;
	private SchedulerLocal scheduler;
	private Runnable runnable;
	private Instant time;
	private Calendar calendar;
	private Timer timer;
	
	@Before
	public void setUp() throws Exception {
		time = Instant.parse("2013-10-09T14:12:47.125Z");
		calendar = Calendar.getInstance();
		calendar.set(2013, 9, 9, 14, 12, 47); // Don't forget month starts from 0
		calendar.set(Calendar.MILLISECOND, 125);
		calendar.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
		control = createStrictControl();
		timer = control.createMock(Timer.class);
		runnable = control.createMock(Runnable.class);
		scheduler = new SchedulerLocal(timer);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testClose() throws Exception {
		timer.cancel();
		control.replay();
		
		scheduler.close();
		
		control.verify();
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		Instant expected = Instant.now();
		Instant actual = scheduler.getCurrentTime();
		assertTrue(ChronoUnit.MILLIS.between(expected, actual) <= 50);
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
	}
	
	@Test
	public void testSchedule_TD() throws Exception {
		SchedulerLocal_TimerTask expected = new SchedulerLocal_TimerTask(runnable, true);
		timer.schedule(eq(expected), eq(calendar.getTime()));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnable, time);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TDL() throws Exception {
		SchedulerLocal_TimerTask expected = new SchedulerLocal_TimerTask(runnable, false);
		timer.schedule(eq(expected), eq(calendar.getTime()), eq(215L));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnable, time, 215L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TL() throws Exception {
		SchedulerLocal_TimerTask expected = new SchedulerLocal_TimerTask(runnable, true);
		timer.schedule(eq(expected), eq(220L));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnable, 220L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSchedule_TLL() throws Exception {
		SchedulerLocal_TimerTask expected = new SchedulerLocal_TimerTask(runnable, false);
		timer.schedule(eq(expected), eq(118L), eq(215L));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnable, 118L, 215L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL() throws Exception {
		SchedulerLocal_TimerTask expected = new SchedulerLocal_TimerTask(runnable, false);
		timer.scheduleAtFixedRate(eq(expected), eq(calendar.getTime()), eq(302L));
		control.replay();
		
		TaskHandler actual = scheduler.scheduleAtFixedRate(runnable, time, 302L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL() throws Exception {
		SchedulerLocal_TimerTask expected = new SchedulerLocal_TimerTask(runnable, false);
		timer.scheduleAtFixedRate(eq(expected), eq(80L), eq(94L));
		control.replay();
		
		TaskHandler actual = scheduler.scheduleAtFixedRate(runnable, 80L, 94L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
}
