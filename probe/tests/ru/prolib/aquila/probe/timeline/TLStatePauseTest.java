package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLCommand;
import ru.prolib.aquila.probe.timeline.TLStatePause;

public class TLStatePauseTest {
	private IMocksControl control;
	private TLSimulationFacade facade;
	private TLStatePause state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSimulationFacade.class);
		state = new TLStatePause(facade);
	}
	
	@Test
	public void testPrepare() throws Exception {
		facade.firePaused();
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
		expect(facade.tellb()).andReturn(new TLCommand());
		control.replay();
		
		assertSame(state.onRun, state.pass());
		
		control.verify();
	}

	@Test
	public void testPass_Pause() throws Exception {
		expect(facade.tellb()).andReturn(TLCommand.PAUSE);
		expect(facade.pull()).andReturn(TLCommand.PAUSE);
		control.replay();
		
		assertNull(state.pass());
		
		control.verify();
	}

	@Test
	public void testPass_Finish() throws Exception {
		expect(facade.tellb()).andReturn(TLCommand.FINISH);
		expect(facade.pull()).andReturn(TLCommand.FINISH);
		control.replay();
		
		assertSame(state.onFinish, state.pass());
		
		control.verify();
	}

}
