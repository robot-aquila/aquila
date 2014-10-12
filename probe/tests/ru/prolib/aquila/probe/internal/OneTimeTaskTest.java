package ru.prolib.aquila.probe.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;

public class OneTimeTaskTest {
	private IMocksControl control;
	private Scheduler scheduler;
	private Runnable runnable;
	private OneTimeTask task;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		scheduler = control.createMock(Scheduler.class);
		runnable = control.createMock(Runnable.class);
		task = new OneTimeTask(scheduler, runnable);
	}
	
	@Test
	public void testRun() throws Exception {
		expect(scheduler.scheduled(same(runnable))).andReturn(true);
		runnable.run();
		scheduler.cancel(same(runnable));
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_Cancelled() throws Exception {
		expect(scheduler.scheduled(same(runnable))).andReturn(false);
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(task.equals(task));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Runnable r2 = control.createMock(Runnable.class);
		Scheduler s2 = control.createMock(Scheduler.class);
		
		assertTrue(task.equals(new OneTimeTask(scheduler, runnable)));
		assertFalse(task.equals(new OneTimeTask(s2, runnable)));
		assertFalse(task.equals(new OneTimeTask(scheduler, r2)));
	}

}
