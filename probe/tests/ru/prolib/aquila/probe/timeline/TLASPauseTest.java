package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLASPauseTest {
	private IMocksControl control;
	private TLSTimeline timeline;
	private TLASPause state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeline = control.createMock(TLSTimeline.class);
		state = new TLASPause(timeline);
	}
	
	@Test
	public void testEnter() throws Exception {
		timeline.setState(TLCmdType.PAUSE);
		timeline.setBlockingMode(true);
		timeline.firePause();
		control.replay();
		
		assertNull(state.enter(null));
		
		control.verify();
	}
	
	@Test
	public void testInput_InFinish() throws Exception {
		control.replay();
		
		assertSame(state.getExit(TLASPause.EEND), state.input(TLCmd.FINISH));
		
		control.verify();
	}

	@Test
	public void testInput_InRun() throws Exception {
		DateTime time = new DateTime(1998, 1, 1, 23, 59, 59, 0);
		timeline.setCutoff(eq(time));
		control.replay();
		
		assertSame(state.getExit(TLASPause.ERUN), state.input(new TLCmd(time)));
		
		control.verify();
	}

	@Test
	public void testInput_InPause() throws Exception {
		control.replay();
		
		assertNull(state.input(TLCmd.PAUSE));
		
		control.verify();
	}
	
	@Test
	public void testActions() throws Exception {
		assertSame(state, state.getEnterAction());
		assertNull(state.getExitAction());
	}

}
