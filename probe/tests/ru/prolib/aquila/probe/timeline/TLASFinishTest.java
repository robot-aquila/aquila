package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLASFinish;

public class TLASFinishTest {
	private IMocksControl control;
	private TLSTimeline facade;
	private TLASFinish state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facade = control.createMock(TLSTimeline.class);
		state = new TLASFinish(facade);
	}

	@Test
	public void testPass() throws Exception {
		facade.clearCommands();
		facade.fireFinish();
		control.replay();
		
		assertSame(state.onFinished, state.pass());
		
		control.verify();
	}

}
