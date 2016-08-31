package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class CmdScheduleTaskTest {
	private IMocksControl control;
	private SchedulerTask task;
	private CmdScheduleTask cmd;

	@Before
	public void setUp() throws Exception {
		control = EasyMock.createControl();
		task = control.createMock(SchedulerTask.class);
		cmd = new CmdScheduleTask(task);
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.SCHEDULE_TASK, cmd.getType());
		assertSame(task, cmd.getTask());
	}
	
	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdScheduleTask(task)));
		assertFalse(cmd.equals(new CmdScheduleTask(control.createMock(SchedulerTask.class))));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
