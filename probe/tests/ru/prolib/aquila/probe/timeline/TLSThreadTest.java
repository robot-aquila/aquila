package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

public class TLSThreadTest {
	private IMocksControl control;
	private TLSThreadWorker worker;
	private TLSThread thread;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		worker = control.createMock(TLSThreadWorker.class);
		thread = new TLSThread(worker);
	}
	
	@Test
	public void testIsAThread() throws Exception {
		assertTrue(thread instanceof Thread);
	}
	
	@Test
	public void testSetDebug() throws Exception {
		worker.setDebug(eq(true));
		worker.setDebug(eq(false));
		control.replay();
		
		thread.setDebug(true);
		thread.setDebug(false);
		
		control.verify();
	}

}
