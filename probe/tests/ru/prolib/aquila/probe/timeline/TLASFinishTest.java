package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLASFinish;

public class TLASFinishTest {
	private IMocksControl control;
	private TLSTimeline timeline;
	private TLASFinish state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeline = control.createMock(TLSTimeline.class);
		state = new TLASFinish(timeline);
	}

	@Test
	public void testEnter() throws Exception {
		timeline.setState(TLCmdType.FINISH);
		timeline.fireFinish();
		timeline.close();
		control.replay();
		
		assertSame(state.getExit(TLASFinish.EOK), state.enter(null));
		
		control.verify();
	}
	
	@Test
	public void testActions() throws Exception {
		assertSame(state, state.getEnterAction());
		assertNull(state.getExitAction());
	}

}
