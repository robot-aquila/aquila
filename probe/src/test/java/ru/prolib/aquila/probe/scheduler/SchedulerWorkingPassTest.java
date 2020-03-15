package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchedulerWorkingPassTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private BlockingQueue<Cmd> queueMock;
	private SchedulerState stateMock;
	private Clock clockMock;
	private Cmd cmdMock;
	private Runnable runnable1Mock, runnable2Mock, runnable3Mock;
	private SchedulerWorkingPass pass;
	private SchedulerWorkingPass.Helper helper, helperMock;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(BlockingQueue.class);
		stateMock = control.createMock(SchedulerState.class);
		clockMock = control.createMock(Clock.class);
		cmdMock = control.createMock(Cmd.class);
		runnable1Mock = control.createMock(Runnable.class);
		runnable2Mock = control.createMock(Runnable.class);
		runnable3Mock = control.createMock(Runnable.class);
		helperMock = control.createMock(SchedulerWorkingPass.Helper.class);
		pass = new SchedulerWorkingPass(queueMock, stateMock, helperMock);
		helper = new SchedulerWorkingPass.Helper(queueMock, stateMock, clockMock);
	}
	
	@Test
	public void testHelper_TravelTo_Speed0_HasTargetTime() throws Exception {
		expect(stateMock.getExecutionSpeed()).andReturn(0);
		stateMock.setCurrentTime(T("2018-12-21T19:07:12Z"));
		control.replay();
		
		assertTrue(helper.travelTo(T("2018-12-21T19:07:12Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_Speed0_NoTargetTime() throws Exception {
		expect(stateMock.getExecutionSpeed()).andReturn(0);
		control.replay();
		
		assertFalse(helper.travelTo(null));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_NoTargetTime() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(stateMock.getExecutionSpeed()).andReturn(2);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(null);
		expect(clockMock.millis()).andReturn(15L);
		stateMock.setCurrentTime(time.plusMillis(100));
		control.replay();
		
		assertFalse(helper.travelTo(null));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_SimDelayGtDay() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(stateMock.getExecutionSpeed()).andReturn(2);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(null);
		expect(clockMock.millis()).andReturn(15L);
		stateMock.setCurrentTime(time.plusMillis(100));
		control.replay();
		
		assertFalse(helper.travelTo(T("2019-01-01T00:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_SimDelayGtQuant() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(stateMock.getExecutionSpeed()).andReturn(2);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(null);
		expect(clockMock.millis()).andReturn(15L);
		stateMock.setCurrentTime(time.plusMillis(100));
		control.replay();
		
		assertFalse(helper.travelTo(T("2016-08-30T20:29:20Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_SimDelayLtQuant() throws Exception {
		Instant time = T("2016-08-30T20:29:15Z");
		expect(stateMock.getExecutionSpeed()).andReturn(2);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(12, TimeUnit.MILLISECONDS)).andReturn(null);
		expect(clockMock.millis()).andReturn(15L);
		stateMock.setCurrentTime(T("2016-08-30T20:29:15.025Z"));
		control.replay();
		
		assertTrue(helper.travelTo(T("2016-08-30T20:29:15.025Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_RealDelayLtMinDelay() throws Exception {
		Instant time = T("2016-08-30T20:29:00.015Z");
		expect(stateMock.getExecutionSpeed()).andReturn(5);
		expect(stateMock.getCurrentTime()).andReturn(time);
		stateMock.setCurrentTime(T("2016-08-30T20:29:00.018Z"));
		control.replay();
		
		assertTrue(helper.travelTo(T("2016-08-30T20:29:00.018Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_RealDelayEqMinDelay() throws Exception {
		Instant time = T("2016-08-30T20:29:00.015Z");
		expect(stateMock.getExecutionSpeed()).andReturn(1);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(1, TimeUnit.MILLISECONDS)).andReturn(null);
		expect(clockMock.millis()).andReturn(11L);
		stateMock.setCurrentTime(T("2016-08-30T20:29:00.016Z"));
		control.replay();
		
		assertTrue(helper.travelTo(T("2016-08-30T20:29:00.016Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_SimDelayGtMinDelay() throws Exception {
		Instant time = T("2016-08-30T20:29:00.000Z");
		expect(stateMock.getExecutionSpeed()).andReturn(1);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(null);
		expect(clockMock.millis()).andReturn(60L);
		stateMock.setCurrentTime(T("2016-08-30T20:29:00.050Z"));
		control.replay();
		
		assertTrue(helper.travelTo(T("2016-08-30T20:29:00.050Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_TravelTo_SpeedGt0_IncomingCommand_TargetReached() throws Exception {
		Instant time = T("2016-08-30T20:29:00.000Z");
		expect(stateMock.getExecutionSpeed()).andReturn(1);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(cmdMock);
		expect(clockMock.millis()).andReturn(60L);
		stateMock.setCurrentTime(T("2016-08-30T20:29:00.050Z"));
		stateMock.processCommand(cmdMock);
		control.replay();
		
		assertTrue(helper.travelTo(T("2016-08-30T20:29:00.050Z")));
		
		control.verify();
	}

	@Test
	public void testHelper_TravelTo_SpeedGt0_IncomingCommand_TargetNotReached() throws Exception {
		Instant time = T("2016-08-30T20:29:00.000Z");
		expect(stateMock.getExecutionSpeed()).andReturn(1);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(clockMock.millis()).andReturn(10L);
		expect(queueMock.poll(50, TimeUnit.MILLISECONDS)).andReturn(cmdMock);
		expect(clockMock.millis()).andReturn(30L);
		stateMock.setCurrentTime(T("2016-08-30T20:29:00.020Z"));
		stateMock.processCommand(cmdMock);
		control.replay();
		
		assertFalse(helper.travelTo(T("2016-08-30T20:29:00.050Z")));
		
		control.verify();
	}
	
	@Test
	public void testHelper_ExecuteTasks_NoSlot() throws Exception {
		Instant time = T("2016-08-30T19:54:00Z");
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(false);
		control.replay();
		
		assertFalse(helper.executeTasks());
		
		control.verify();
	}
	
	@Test
	public void testHelper_ExecuteTasks_HasSlot() throws Exception {
		SchedulerTaskImpl task1 = new SchedulerTaskImpl(runnable1Mock),
				task2 = new SchedulerTaskImpl(runnable2Mock),
				task3 = new SchedulerTaskImpl(runnable3Mock);
		Instant time = T("2016-08-30T19:54:00Z");
		task1.scheduleForFirstExecution(time);
		task2.scheduleForFirstExecution(time);
		task3.scheduleForFirstExecution(time);
		SchedulerSlot slot = new SchedulerSlot(time)
			.addTask(task1)
			.addTask(task2)
			.addTask(task3);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		//stateMock.beforeExecution(time);
		stateMock.beforeExecution(time);
		runnable1Mock.run();
		stateMock.afterExecution(time);
		stateMock.waitForThread(time);
		stateMock.beforeExecution(time);
		runnable2Mock.run();
		stateMock.afterExecution(time);
		stateMock.waitForThread(time);
		stateMock.beforeExecution(time);
		runnable3Mock.run();
		stateMock.afterExecution(time);
		stateMock.waitForThread(time);
		//stateMock.afterExecution(time);
		//stateMock.waitForThread(time);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		control.replay();
		
		assertTrue(helper.executeTasks());
		
		control.verify();
	}
	
	@Test
	public void testHelper_ExecuteTasks_ReschedulePeriodic() throws Exception {
		SchedulerTaskImpl task = new SchedulerTaskImpl(runnable1Mock, 1000);
		Instant time = T("2016-08-30T20:29:15Z");
		task.scheduleForFirstExecution(time);
		SchedulerSlot slot = new SchedulerSlot(time)
			.addTask(task);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		stateMock.beforeExecution(time);
		runnable1Mock.run();
		stateMock.afterExecution(time);
		stateMock.waitForThread(time);
		stateMock.addTask(task);
		//stateMock.afterExecution(time);
		//stateMock.waitForThread(time);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		control.replay();
		
		assertTrue(helper.executeTasks());
		
		control.verify();
		assertTrue(task.isScheduled());
		assertEquals(T("2016-08-30T20:29:16Z"), task.getNextExecutionTime());
	}
	
	@Test
	public void testHelper_ExecuteTasks_SkipCancelled() throws Exception {
		SchedulerTaskImpl task = new SchedulerTaskImpl(runnable1Mock);
		Instant time = T("2016-08-30T20:29:15Z");
		task.cancel();
		SchedulerSlot slot = new SchedulerSlot(time).addTask(task);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		//stateMock.beforeExecution(time);
		//stateMock.afterExecution(time);
		//stateMock.waitForThread(time);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		control.replay();
		
		assertTrue(helper.executeTasks());
		
		control.verify();
		assertFalse(task.isScheduled());
	}
	
	@Test
	public void testHelper_ExecuteTasks_RunStepMode() throws Exception {
		SchedulerTaskImpl task = new SchedulerTaskImpl(runnable1Mock);
		Instant time = T("2016-08-30T20:29:15Z");
		task.scheduleForFirstExecution(time);
		SchedulerSlot slot = new SchedulerSlot(time).addTask(task);
		expect(stateMock.getCurrentTime()).andReturn(time);
		expect(stateMock.hasSlotForExecution()).andReturn(true);
		expect(stateMock.removeNextSlot()).andReturn(slot);
		stateMock.beforeExecution(time);
		runnable1Mock.run();
		stateMock.afterExecution(time);
		stateMock.waitForThread(time);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		stateMock.switchToWait();
		control.replay();
		
		assertTrue(helper.executeTasks());
		
		control.verify();
		assertFalse(task.isScheduled());
	}
	
	@Test
	public void testCtor2() {
		pass = new SchedulerWorkingPass(queueMock, stateMock);
		assertSame(queueMock, pass.getCommandQueue());
		assertSame(stateMock, pass.getSchedulerState());
	}
	
	@Test
	public void testCtor3() {
		assertSame(queueMock, pass.getCommandQueue());
		assertSame(stateMock, pass.getSchedulerState());
	}

	@Test
	public void testExecute_Phase1_HasCommandInQueue() throws Exception {
		expect(queueMock.poll()).andReturn(cmdMock);
		stateMock.processCommand(cmdMock);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase1_ModeClose() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.CLOSE);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase1_ModeWait() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.WAIT);
		expect(queueMock.take()).andReturn(cmdMock);
		stateMock.processCommand(cmdMock);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeRun_HasTasksExecuted() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(helperMock.travelTo(T("1995-06-12T15:35:02Z"))).andReturn(true);
		expect(helperMock.executeTasks()).andReturn(true);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeRun_TargetTimeNotReached() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(helperMock.travelTo(T("1995-06-12T15:35:02Z"))).andReturn(false);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

	@Test
	public void testExecute_Phase2_ModeRunStep_HasTasksExecuted() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		expect(helperMock.travelTo(T("1995-06-12T15:35:02Z"))).andReturn(true);
		expect(helperMock.executeTasks()).andReturn(true);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeRunStep_TargetTimeNotReached() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_STEP);
		expect(helperMock.travelTo(T("1995-06-12T15:35:02Z"))).andReturn(false);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeWait() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.WAIT);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeClose() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.CLOSE);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeCutoff_TargetTimeNotReached() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getCutoffTime()).andReturn(T("1995-06-12T15:40:00Z"));
		expect(helperMock.travelTo(T("1995-06-12T15:35:02Z"))).andReturn(false);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeCutoff_TargetTimeIsCutoff() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:40:00Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getCutoffTime()).andReturn(T("1995-06-12T15:40:00Z"));
		expect(helperMock.travelTo(T("1995-06-12T15:40:00Z"))).andReturn(true);
		stateMock.switchToWait();
		control.replay();
		
		pass.execute();
		
		control.verify();
	}
	
	@Test
	public void testExecute_Phase2_ModeCutoff_HasTasksExecuted() throws Exception {
		expect(queueMock.poll()).andReturn(null);
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getNextTargetTime()).andReturn(T("1995-06-12T15:35:02Z"));
		expect(stateMock.getMode()).andReturn(SchedulerMode.RUN_CUTOFF);
		expect(stateMock.getCutoffTime()).andReturn(T("1995-06-12T15:40:00Z"));
		expect(helperMock.travelTo(T("1995-06-12T15:35:02Z"))).andReturn(true);
		expect(helperMock.executeTasks()).andReturn(true);
		control.replay();
		
		pass.execute();
		
		control.verify();
	}

}
