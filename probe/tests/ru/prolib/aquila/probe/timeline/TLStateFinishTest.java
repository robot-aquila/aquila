package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLStateFinish;

public class TLStateFinishTest {
	private IMocksControl control;
	private TLSimulationFacade facade;
	private TLStateFinish state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSimulationFacade.class);
		state = new TLStateFinish(facade);
	}

	@Test
	public void testPass() throws Exception {
		facade.clearCommands();
		facade.fireFinished();
		control.replay();
		
		assertSame(state.onFinished, state.pass());
		
		control.verify();
	}

}
