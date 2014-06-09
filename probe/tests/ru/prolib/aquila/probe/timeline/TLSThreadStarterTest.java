package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.*;

public class TLSThreadStarterTest {
	private IMocksControl control;
	private CountDownLatch started;
	private TLSThread thread;
	private TLSThreadStarter starter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		started = control.createMock(CountDownLatch.class);
		thread = control.createMock(TLSThread.class);
		starter = new TLSThreadStarter(started, thread);
	}
	
	@Test
	public void testStart() throws Exception {
		thread.start();
		expect(started.await(5, TimeUnit.SECONDS)).andReturn(true);
		control.replay();
		
		starter.start();
		starter.start();
		starter.start();
		
		control.verify();
	}
	
	@Test (expected=TLInterruptionsNotAllowedException.class)
	public void testStart_ThrowsIfInterrupted() throws Exception {
		thread.start();
		expect(started.await(5, TimeUnit.SECONDS))
			.andThrow(new InterruptedException("test"));
		control.replay();
		
		starter.start();
	}
	
	@Test
	public void testSetDebug() throws Exception {
		thread.setDebug(eq(true));
		thread.setDebug(eq(false));
		control.replay();
		
		starter.setDebug(true);
		starter.setDebug(false);
		
		control.verify();
	}

}
