package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

public class SchedulerLocal_TimerTaskTest {
	private IMocksControl control;
	private Runnable runnable1, runnable2;

	private SchedulerLocal_TimerTask timerTask1, timerTask2;

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

}
