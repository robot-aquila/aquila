package ru.prolib.aquila.probe;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.SPRunnableTaskHandler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.probe.scheduler.Cmd;
import ru.prolib.aquila.probe.scheduler.CmdClose;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchRun;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchRunCutoff;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchRunStep;
import ru.prolib.aquila.probe.scheduler.CmdModeSwitchWait;
import ru.prolib.aquila.probe.scheduler.CmdScheduleTask;
import ru.prolib.aquila.probe.scheduler.CmdSetExecutionSpeed;
import ru.prolib.aquila.probe.scheduler.CmdShiftForward;
import ru.prolib.aquila.probe.scheduler.SchedulerState;
import ru.prolib.aquila.probe.scheduler.SchedulerTaskImpl;

public class SchedulerImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private BlockingQueue<Cmd> queueMock;
	private SchedulerState stateMock;
	private Runnable runnableMock;
	private SchedulerImpl scheduler;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(BlockingQueue.class);
		stateMock = control.createMock(SchedulerState.class);
		runnableMock = control.createMock(Runnable.class);
		scheduler = new SchedulerImpl(queueMock, stateMock);
	}
	
	@Test
	public void testCtor2() {
		assertSame(queueMock, scheduler.getCommandQueue());
		assertSame(stateMock, scheduler.getState());
		assertFalse(scheduler.isClosed());
	}
	
	@Test
	public void testGetCurrentTime() {
		expect(stateMock.getCurrentTime()).andReturn(T("2016-08-31T18:00:56Z"));
		control.replay();
		
		assertEquals(T("2016-08-31T18:00:56Z"), scheduler.getCurrentTime());
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		queueMock.put(new CmdClose());
		control.replay();
		
		scheduler.close();
		
		control.verify();
		assertTrue(scheduler.isClosed());
	}
	
	@Test
	public void testClose_CallingTwiceHasNoEffect() throws Exception {
		queueMock.put(new CmdClose());
		control.replay();
		
		scheduler.close();
		scheduler.close();
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RI_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.schedule(runnableMock, T("2016-08-31T18:38:19Z"));
	}
	
	@Test
	public void testSchedule_RI() throws Exception {
		SchedulerTaskImpl expected = new SchedulerTaskImpl(runnableMock);
		expected.scheduleForFirstExecution(T("1998-03-15T00:00:00Z"));
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnableMock, T("1998-03-15T00:00:00Z"));
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RIL_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.schedule(runnableMock, T("2015-02-14T00:00:00Z"), 1000L);
	}

	@Test
	public void testSchedule_RIL() throws Exception {
		SchedulerTaskImpl expected = new SchedulerTaskImpl(runnableMock, 1000L);
		expected.scheduleForFirstExecution(T("2015-02-14T00:00:00Z"));
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnableMock, T("2015-02-14T00:00:00Z"), 1000L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RL_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.schedule(runnableMock, 2500L);
	}
	
	@Test
	public void testSchedule_RL() throws Exception {
		SchedulerTaskImpl expected = new SchedulerTaskImpl(runnableMock);
		expected.scheduleForFirstExecution(T("2024-08-13T00:00:00Z"), 2500L);
		expect(stateMock.getCurrentTime()).andReturn(T("2024-08-13T00:00:00Z"));
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnableMock, 2500L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSchedule_RLL_ThrowsIfCLosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.schedule(runnableMock, 2500L, 5000L);
	}

	@Test
	public void testSchedule_RLL() throws Exception {
		SchedulerTaskImpl expected = new SchedulerTaskImpl(runnableMock, 5000L);
		expected.scheduleForFirstExecution(T("1345-09-12T00:00:00Z"), 2500L);
		expect(stateMock.getCurrentTime()).andReturn(T("1345-09-12T00:00:00Z"));
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnableMock, 2500L, 5000L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleAtFixedRate_RIL_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.scheduleAtFixedRate(runnableMock, T("2015-02-14T00:00:00Z"), 1000L);
	}
	
	@Test
	public void testScheduleAtFixedRate_RIL() throws Exception {
		SchedulerTaskImpl expected = new SchedulerTaskImpl(runnableMock, 1000L);
		expected.scheduleForFirstExecution(T("2015-02-14T00:00:00Z"));
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.scheduleAtFixedRate(runnableMock, T("2015-02-14T00:00:00Z"), 1000L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testScheduleAtFixedRate_RLL_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.scheduleAtFixedRate(runnableMock, 2500L, 5000L);
	}

	@Test
	public void testScheduleAtFixedRate_RLL() throws Exception {
		SchedulerTaskImpl expected = new SchedulerTaskImpl(runnableMock, 5000L);
		expected.scheduleForFirstExecution(T("1345-09-12T00:00:00Z"), 2500L);
		expect(stateMock.getCurrentTime()).andReturn(T("1345-09-12T00:00:00Z"));
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.scheduleAtFixedRate(runnableMock, 2500L, 5000L);
		
		control.verify();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSetModeRun0() throws Exception {
		queueMock.put(new CmdModeSwitchRun());
		control.replay();
		
		scheduler.setModeRun();
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetModeRun0_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.setModeRun();
	}
	
	@Test
	public void testSetModeRun1() throws Exception {
		queueMock.put(new CmdModeSwitchRunCutoff(T("2024-07-14T00:00:00Z")));
		control.replay();
		
		scheduler.setModeRun(T("2024-07-14T00:00:00Z"));
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetModeRun1_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.setModeRun(T("2024-07-14T00:00:00Z"));
	}
	
	@Test
	public void testSetModeStep() throws Exception {
		queueMock.put(new CmdModeSwitchRunStep());
		control.replay();
		
		scheduler.setModeStep();
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetModeStep_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.setModeStep();
	}
	
	@Test
	public void testSetModeWait() throws Exception {
		queueMock.put(new CmdModeSwitchWait());
		control.replay();
		
		scheduler.setModeWait();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetModeWait_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.setModeWait();
	}
	
	@Test
	public void testSetCurrentTime() throws Exception {
		queueMock.put(new CmdShiftForward(T("2016-08-31T00:00:00Z")));
		control.replay();
		
		scheduler.setCurrentTime(T("2016-08-31T00:00:00Z"));
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetCurrentTime_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.setCurrentTime(T("2016-08-31T00:00:00Z"));
	}
	
	@Test
	public void testSetExecutionSpeed() throws Exception {
		queueMock.put(new CmdSetExecutionSpeed(8));
		control.replay();
		
		scheduler.setExecutionSpeed(8);
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetExecutionSpeed_ThrowsIfClosed() throws Exception {
		control.resetToNice();
		control.replay();
		scheduler.close();
		
		scheduler.setExecutionSpeed(8);
	}
	
	@Test
	public void testSchedule_SelfPlanned() throws Exception {
		SPRunnable runnableMock = control.createMock(SPRunnable.class);
		expect(stateMock.getCurrentTime()).andReturn(T("2016-12-02T13:26:00Z"));
		expect(runnableMock.getNextExecutionTime(T("2016-12-02T13:26:00Z"))).andReturn(T("2016-12-02T13:30:00Z"));
		SchedulerTaskImpl expected = new SchedulerTaskImpl(new SPRunnableTaskHandler(scheduler, runnableMock));
		expected.scheduleForFirstExecution(T("2016-12-02T13:30:00Z"));		
		queueMock.put(new CmdScheduleTask(expected));
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnableMock);
		
		control.verify();
		assertEquals(new SPRunnableTaskHandler(scheduler, runnableMock), actual);
	}
	
	@Test
	public void testSchedule_SelfPlanned_CancelledOnStart() throws Exception {
		SPRunnable runnableMock = control.createMock(SPRunnable.class);
		expect(stateMock.getCurrentTime()).andReturn(T("2016-12-02T13:26:00Z"));
		expect(runnableMock.getNextExecutionTime(T("2016-12-02T13:26:00Z"))).andReturn(null);
		control.replay();
		
		TaskHandler actual = scheduler.schedule(runnableMock);
		
		control.verify();
		SPRunnableTaskHandler expected = new SPRunnableTaskHandler(scheduler, runnableMock);
		expected.cancel();
		assertEquals(expected, actual);
	}

}
