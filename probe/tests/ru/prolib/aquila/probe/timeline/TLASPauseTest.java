package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLCmd;
import ru.prolib.aquila.probe.timeline.TLASPause;

public class TLASPauseTest {
	private IMocksControl control;
	private TLSTimeline facade;
	private TLASPause state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSTimeline.class);
		state = new TLASPause(facade);
	}
	
	@Test
	public void testPrepare() throws Exception {
		facade.firePause();
		control.replay();
		
		state.prepare();
		
		control.verify();
	}
	
	@Test
	public void testPass_Interrupted() throws Exception {
		expect(facade.tellb()).andThrow(new InterruptedException("test error"));
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}
	
	@Test
	public void testPass_Run() throws Exception {
		expect(facade.tellb()).andReturn(new TLCmd());
		control.replay();
		
		assertSame(state.onRun, state.pass());
		
		control.verify();
	}

	@Test
	public void testPass_Pause() throws Exception {
		expect(facade.tellb()).andReturn(TLCmd.PAUSE);
		expect(facade.pull()).andReturn(TLCmd.PAUSE);
		control.replay();
		
		assertNull(state.pass());
		
		control.verify();
	}

	@Test
	public void testPass_Finish() throws Exception {
		expect(facade.tellb()).andReturn(TLCmd.FINISH);
		expect(facade.pull()).andReturn(TLCmd.FINISH);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}

}
