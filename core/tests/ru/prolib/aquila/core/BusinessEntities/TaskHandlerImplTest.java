package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*; 
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.SchedulerTestComponents.*;

import ru.prolib.aquila.core.utils.Variant;

public class TaskHandlerImplTest {
	private IMocksControl control;
	private Runnable task;
	private Scheduler scheduler;
	private TaskHandler handler;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		task = control.createMock(Runnable.class);
		scheduler = control.createMock(Scheduler.class);
		handler = new TaskHandlerImpl(task, scheduler);
	}
	
	@Test
	public void testGetTask() throws Exception {
		assertSame(task, handler.getTask());
	}
	
	@Test
	public void testGetScheduler() throws Exception {
		assertSame(scheduler, handler.getScheduler());
	}
	
	@Test
	public void testCancel() throws Exception {
		scheduler.cancel(same(task));
		control.replay();
		
		handler.cancel();
		
		control.verify();
	}
	
	@Test
	public void testScheduled() throws Exception {
		expect(scheduler.scheduled(same(task))).andReturn(false);
		expect(scheduler.scheduled(same(task))).andReturn(true);
		control.replay();
		
		assertFalse(handler.scheduled());
		assertTrue(handler.scheduled());
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		task = new MyRunnable();
		scheduler = new MyScheduler();
		handler = new TaskHandlerImpl(task, scheduler);
		Variant<Runnable> vTask = new Variant<Runnable>()
			.add(task)
			.add(new MyRunnable());
		Variant<Scheduler> vSchd = new Variant<Scheduler>(vTask)
			.add(scheduler)
			.add(new MyScheduler());
		Variant<?> iterator = vSchd;
		int foundCnt = 0;
		TaskHandler x, found = null;
		do {
			x = new TaskHandlerImpl(vTask.get(), vSchd.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(scheduler, found.getScheduler());
		assertSame(task, found.getTask());
	}

}
