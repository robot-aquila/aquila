package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SchedulerWorkingPassTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private BlockingQueue<Cmd> queueMock;
	private SchedulerState stateMock;
	private Cmd cmdMock;
	private Runnable runnable1Mock, runnable2Mock, runnable3Mock;
	private SchedulerWorkingPass pass;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(BlockingQueue.class);
		stateMock = control.createMock(SchedulerState.class);
		cmdMock = control.createMock(Cmd.class);
		runnable1Mock = control.createMock(Runnable.class);
		runnable2Mock = control.createMock(Runnable.class);
		runnable3Mock = control.createMock(Runnable.class);
		pass = new SchedulerWorkingPass(queueMock, stateMock);
	}
	
	@Test
	public void testCtor2() {
		assertSame(queueMock, pass.getCommandQueue());
		assertSame(stateMock, pass.getSchedulerState());
	}

	@Test
	public void testExecute_HasCommandInQueue() throws Exception {
		expect(queueMock.poll()).andReturn(cmdMock);
		stateMock.processCommand(cmdMock);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_CloseMode() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.CLOSE);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_WaitMode() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.WAIT);
		expect(queueMock.take()).andReturn(cmdMock);
		stateMock.processCommand(cmdMock);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_HasSlotForExecution() throws Exception {
		SchedulerTask task1 = new SchedulerTask(runnable1Mock),
				task2 = new SchedulerTask(runnable2Mock),
				task3 = new SchedulerTask(runnable3Mock);
		Instant time = T("2016-08-30T19:54:00Z");
		task1.scheduleForFirstExecution(time);
		task2.scheduleForFirstExecution(time);
		task3.scheduleForFirstExecution(time);
		SchedulerSlot slot = new SchedulerSlot(time)
			.addTask(task1)
			.addTask(task2)
			.addTask(task3);
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		runnable1Mock.run();
		runnable2Mock.run();
		runnable3Mock.run();
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_HasSlotForExecution_ReschedulePeriodic() throws Exception {
		SchedulerTask task = new SchedulerTask(runnable1Mock, 1000);
		Instant time = T("2016-08-30T20:29:15Z");
		task.scheduleForFirstExecution(time);
		SchedulerSlot slot = new SchedulerSlot(time)
			.addTask(task);
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		runnable1Mock.run();
		stateMock.addTask(task);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		control.replay();
		
		pass.execute();
		
		control.verify();
		assertTrue(task.isScheduled());
		assertEquals(T("2016-08-30T20:29:16Z"), task.getNextExecutionTime());
	}
	
	@Test
	public void testExecute_HasSlotForExecution_SwitchToWaitInRunStepMode() throws Exception {
		SchedulerTask task = new SchedulerTask(runnable1Mock);
		Instant time = T("2016-08-30T20:29:15Z");
		task.scheduleForFirstExecution(time);
		SchedulerSlot slot = new SchedulerSlot(time)
			.addTask(task);
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		runnable1Mock.run();
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		stateMock.switchToWait();
		control.replay();
		
		pass.execute();
		
		control.verify();
		assertFalse(task.isScheduled());
	}

	@Test
	public void testExecute_HasSlotForExecution_SkipCancelled() throws Exception {
		SchedulerTask task = new SchedulerTask(runnable1Mock);
		Instant time = T("2016-08-30T20:29:15Z");
		task.cancel();
		SchedulerSlot slot = new SchedulerSlot(time)
			.addTask(task);
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		control.replay();
		
		pass.execute();
		
		control.verify();
		assertFalse(task.isScheduled());
	}
	
	@Test
	public void testExecute_NoSlot_SwitchedToWaitOnTargetTimeCalculation() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(T("2016-08-30T23:59:59Z"));
		expect(stateMock.isModeWait()).andReturn(true);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_NoSlot_ZeroSpeed_HasTargetTime() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(T("2016-08-30T23:59:59Z"));
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(0);
		stateMock.setCurrentTime(T("2016-08-30T23:59:59Z"));
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

	@Test
	public void testExecute_NoSlot_ZeroSpeed_NoTargetTime() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(null);
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(0);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_NoSlot_Speed1_NoTargetTime() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(null);
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(1);
		expect(queueMock.poll(100, TimeUnit.MILLISECONDS)).andReturn(null);
		stateMock.setCurrentTime(time.plusMillis(100));
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

	@Test
	public void testExecute_NoSlot_Speed1_NoTargetTime_HasCommand() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(null);
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(1);
		expect(queueMock.poll(100, TimeUnit.MILLISECONDS)).andReturn(cmdMock);
		stateMock.setCurrentTime(time.plusMillis(100));
		stateMock.processCommand(cmdMock);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

	@Test
	public void testExecute_NoSlot_Speed2_HasTargetTime_TargetDelayGtMinQuant() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(T("2016-08-30T20:59:59Z"));
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(2);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(null);
		stateMock.setCurrentTime(time.plusMillis(100));
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

	@Test
	public void testExecute_NoSlot_Speed3_HasTargetTime_TargetDelayLtMinQuant() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(T("2016-08-30T20:29:15.050Z"));
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(3);
		expect(queueMock.poll(16, TimeUnit.MILLISECONDS)).andReturn(null);
		stateMock.setCurrentTime(time.plusMillis(50));
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

	@Test
	public void testExecute_NoSlot_Speed4_HasTargetTime_TargetDelayLtMinDelay() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(T("2016-08-30T20:29:15.030Z"));
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(4);
		stateMock.setCurrentTime(time.plusMillis(30));
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_NoSlot_Speed5_HasTargetTime_TargetDelayEqMinDelay() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		expect(stateMock.getNextTargetTime()).andReturn(T("2016-08-30T20:29:15.050Z"));
		expect(stateMock.isModeWait()).andReturn(false);
		expect(stateMock.getExecutionSpeed()).andReturn(5);
		expect(queueMock.poll(10, TimeUnit.MILLISECONDS)).andReturn(null);
		stateMock.setCurrentTime(time.plusMillis(50));
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

}
