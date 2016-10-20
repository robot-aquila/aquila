package ru.prolib.aquila.probe;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.probe.scheduler.Cmd;
import ru.prolib.aquila.probe.scheduler.CmdSetExecutionSpeed;
import ru.prolib.aquila.probe.scheduler.CmdShiftForward;
import ru.prolib.aquila.probe.scheduler.SchedulerState;
import ru.prolib.aquila.probe.scheduler.SchedulerWorker;

public class SchedulerBuilderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private BlockingQueue<Cmd> queueMock;
	private SchedulerState stateMock;
	private SchedulerWorker workerMock;
	private Thread workerThreadMock;
	private SchedulerBuilder builder;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(BlockingQueue.class);
		stateMock = control.createMock(SchedulerState.class);
		workerMock = control.createMock(SchedulerWorker.class);
		workerThreadMock = control.createMock(Thread.class);
		builder = new SchedulerBuilder();
	}
	
	@Test
	public void testGetCommandQueue() {
		assertSame(builder, builder.setCommandQueue(queueMock));
		
		assertSame(queueMock, builder.getCommandQueue());
	}
	
	@Test
	public void testGetCommandQueue_DefaultInstance() {
		LinkedBlockingQueue<Cmd> queue = (LinkedBlockingQueue<Cmd>) builder.getCommandQueue();
		
		assertNotNull(queue);
		assertSame(queue, builder.getCommandQueue());
	}
	
	@Test
	public void testGetState() {
		assertSame(builder, builder.setState(stateMock));
		
		assertSame(stateMock, builder.getState());
	}
	
	@Test
	public void testGetState_DefaultInstance() {
		SchedulerState state = builder.getState();
		
		assertNotNull(state);
		assertSame(state, builder.getState());
	}
	
	@Test
	public void testGetWorker() {
		assertSame(builder, builder.setWorker(workerMock));
		
		assertSame(workerMock, builder.getWorker());
	}
	
	@Test
	public void testGetWorker_DefaultInstance() {
		builder.setCommandQueue(queueMock);
		builder.setState(stateMock);
		
		SchedulerWorker worker = builder.getWorker();
		
		assertNotNull(worker);
		assertSame(worker, builder.getWorker());
		assertSame(stateMock, worker.getSchedulerState());
		assertSame(stateMock, worker.getWorkingPass().getSchedulerState());
		assertSame(queueMock, worker.getWorkingPass().getCommandQueue());
	}
	
	@Test
	public void testGetName() {
		assertSame(builder, builder.setName("PROBE"));
		
		assertEquals("PROBE", builder.getName());
	}
	
	@Test
	public void testGetName_DefaultInstance() {
		int index = SchedulerBuilder.getLastNameIndex();
		
		String expected = "PROBE-SCHEDULER-" + (index + 1);
		assertEquals(expected, builder.getName());
		assertEquals(expected, builder.getName());
	}
	
	@Test
	public void testGetWorkerThread() {
		assertSame(builder, builder.setWorkerThread(workerThreadMock));
		
		assertSame(workerThreadMock, builder.getWorkerThread());
	}
	
	@Test
	public void testGetWorkerThread_DefaultInstance() throws Exception {
		builder.setName("foobar");
		builder.setWorker(workerMock);
		
		Thread thread = builder.getWorkerThread();
		
		assertNotNull(thread);
		assertEquals("foobar", thread.getName());
		workerMock.run();
		control.replay();
		thread.run();
		thread.join(1000L);
		assertFalse(thread.isAlive());
		control.verify();
	}
	
	@Test
	public void testBuildScheduler() {
		builder.setWorkerThread(workerThreadMock)
			.setCommandQueue(queueMock)
			.setState(stateMock);
		workerThreadMock.setDaemon(true);
		workerThreadMock.start();
		control.replay();
		
		Scheduler actual = builder.buildScheduler();
		
		control.verify();
		assertNotNull(actual);
		SchedulerImpl scheduler = (SchedulerImpl) actual;
		assertSame(queueMock, scheduler.getCommandQueue());
		assertSame(stateMock, scheduler.getState());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildScheduler_ThrowsIfCalledTwice() {
		builder.setWorkerThread(workerThreadMock)
			.setCommandQueue(queueMock)
			.setState(stateMock);
		workerThreadMock.setDaemon(true);
		workerThreadMock.start();
		control.replay();
		builder.buildScheduler();
		
		builder.buildScheduler();
	}
	
	@Test
	public void testGetInitialTime() {
		assertNull(builder.getInitialTime());
		
		assertSame(builder, builder.setInitialTime(T("2016-08-31T15:40:00Z")));
		
		assertEquals(T("2016-08-31T15:40:00Z"), builder.getInitialTime());
	}
	
	@Test
	public void testBuildScheduler_SetInitialTimeIfDefined() throws Exception {
		builder.setInitialTime(T("1978-06-02T00:00:00Z"));
		builder.setWorkerThread(workerThreadMock)
			.setCommandQueue(queueMock)
			.setState(stateMock);
		workerThreadMock.setDaemon(true);
		workerThreadMock.start();
		queueMock.put(new CmdShiftForward(T("1978-06-02T00:00:00Z")));
		control.replay();

		assertNotNull(builder.buildScheduler());
		
		control.verify();
	}
	
	@Test
	public void testGetExecutionSpeed() {
		assertNull(builder.getExecutionSpeed());
		
		assertSame(builder, builder.setExecutionSpeed(2));
		
		assertEquals(new Integer(2), builder.getExecutionSpeed());
	}
	
	@Test
	public void testBuildScheduler_WhenExecutionSpeedDefined() throws Exception {
		builder.setExecutionSpeed(2)
			.setWorkerThread(workerThreadMock)
			.setCommandQueue(queueMock)
			.setState(stateMock);
		workerThreadMock.setDaemon(true);
		workerThreadMock.start();
		queueMock.put(new CmdSetExecutionSpeed(2));
		control.replay();
		
		assertNotNull(builder.buildScheduler());
		
		control.verify();
	}

}
