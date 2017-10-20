package ru.prolib.aquila.probe.scheduler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class SchedulerWorkerTest {
	private IMocksControl control;
	private SchedulerWorkingPass passMock;
	private SchedulerState stateMock;
	private SchedulerWorker worker;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		passMock = control.createMock(SchedulerWorkingPass.class);
		stateMock = control.createMock(SchedulerState.class);
		worker = new SchedulerWorker(passMock, stateMock);
	}
	
	@Test
	public void testCtor2_PassAndState() {
		assertSame(passMock, worker.getWorkingPass());
		assertSame(stateMock, worker.getSchedulerState());
	}
	
	@Test
	public void testCtor2_QueueAndState() {
		BlockingQueue<Cmd> queue = new LinkedBlockingQueue<>();
		worker = new SchedulerWorker(queue, stateMock);
		SchedulerWorkingPass pass = worker.getWorkingPass();
		assertSame(queue, pass.getCommandQueue());
		assertSame(stateMock, pass.getSchedulerState());
		assertSame(stateMock, worker.getSchedulerState());
	}
	
	@Test
	public void testRun() throws Exception {
		expect(stateMock.isClosed()).andReturn(false);
		passMock.execute();
		expect(stateMock.isClosed()).andReturn(false);
		passMock.execute();
		expect(stateMock.isClosed()).andReturn(true);
		control.replay();
		
		worker.run();

		control.verify();
	}

}
