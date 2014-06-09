package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLASRunTest {
	private IMocksControl control;
	private TLSTimeline timeline;
	private DateTime time;
	private TLASRun state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeline = control.createMock(TLSTimeline.class);
		time = new DateTime(1997, 7, 12, 20, 15, 0, 999);
		state = new TLASRun(timeline);
	}
	
	@Test
	public void testEnter() throws Exception {
		timeline.setState(TLCmdType.RUN);
		timeline.setBlockingMode(false);
		timeline.fireRun();
		control.replay();
		
		state.enter(null);
		
		control.verify();
	}
	
	@Test
	public void testInput_InPause() throws Exception {
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EPAUSE), state.input(TLCmd.PAUSE));
		
		control.verify();
	}

	@Test
	public void testInput_InFinish() throws Exception {
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EEND), state.input(TLCmd.FINISH));
		
		control.verify();
	}
	
	@Test
	public void testInput_InRun_OutOfInterval() throws Exception {
		timeline.setCutoff(eq(time));
		expect(timeline.isOutOfInterval()).andReturn(true);
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EEND), state.input(new TLCmd(time)));
		
		control.verify();
	}
	
	@Test
	public void testInput_InRun_Cutoff() throws Exception {
		timeline.setCutoff(eq(time));
		expect(timeline.isOutOfInterval()).andReturn(false);
		expect(timeline.isCutoff()).andReturn(true);
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EPAUSE),
				state.input(new TLCmd(time)));
		
		control.verify();
	}

	@Test
	public void testInput_InRun_Continue() throws Exception {
		timeline.setCutoff(eq(time));
		expect(timeline.isOutOfInterval()).andReturn(false);
		expect(timeline.isCutoff()).andReturn(false);
		expect(timeline.execute()).andReturn(true);
		timeline.fireStep();
		control.replay();
		
		assertNull(state.input(new TLCmd(time)));
		
		control.verify();
	}

	@Test
	public void testInput_InRun_Executed() throws Exception {
		timeline.setCutoff(eq(time));
		expect(timeline.isOutOfInterval()).andReturn(false);
		expect(timeline.isCutoff()).andReturn(false);
		expect(timeline.execute()).andReturn(false);
		timeline.fireStep();
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EEND), state.input(new TLCmd(time)));
		
		control.verify();
	}

	@Test
	public void testInput_InNull_OutOfInterval() throws Exception {
		expect(timeline.isOutOfInterval()).andReturn(true);
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EEND), state.input(null));
		
		control.verify();
	}

	@Test
	public void testInput_InNull_Cutoff() throws Exception {
		expect(timeline.isOutOfInterval()).andReturn(false);
		expect(timeline.isCutoff()).andReturn(true);
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EPAUSE), state.input(null));
		
		control.verify();
	}
	
	@Test
	public void testInput_InNull_Continue() throws Exception {
		expect(timeline.isOutOfInterval()).andReturn(false);
		expect(timeline.isCutoff()).andReturn(false);
		expect(timeline.execute()).andReturn(true);
		timeline.fireStep();
		control.replay();
		
		assertNull(state.input(null));
		
		control.verify();
	}
	
	@Test
	public void testInput_InNull_Executed() throws Exception {
		expect(timeline.isOutOfInterval()).andReturn(false);
		expect(timeline.isCutoff()).andReturn(false);
		expect(timeline.execute()).andReturn(false);
		timeline.fireStep();
		control.replay();
		
		assertEquals(state.getExit(TLASRun.EEND), state.input(null));
		
		control.verify();
	}
	
	@Test
	public void testActions() throws Exception {
		assertSame(state, state.getEnterAction());
		assertNull(state.getExitAction());
	}

}
