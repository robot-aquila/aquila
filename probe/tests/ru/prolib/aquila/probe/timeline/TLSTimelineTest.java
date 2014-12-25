package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.probe.timeline.TLCmd;
import ru.prolib.aquila.probe.timeline.TLCmdQueue;
import ru.prolib.aquila.probe.timeline.TLSEventDispatcher;
import ru.prolib.aquila.probe.timeline.TLSTimeline;
import ru.prolib.aquila.probe.timeline.TLSStrategy;

public class TLSTimelineTest {
	private static DateTime from = new DateTime(2014, 5, 21, 20, 0, 0, 0);
	private static DateTime to = new DateTime(2014, 6, 1, 0, 0, 0, 0);
	private static Interval interval = new Interval(from, to);
	
	private IMocksControl control;
	private TLCmdQueue cmdQueue;
	private TLEventQueue evtQueue;
	private TLSStrategy simulation;
	private TLSEventDispatcher dispatcher;
	private TLEventSources sources;
	private TLSTimeline timeline;
	private EventType evtType;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cmdQueue = control.createMock(TLCmdQueue.class);
		evtQueue = control.createMock(TLEventQueue.class);
		simulation = control.createMock(TLSStrategy.class);
		dispatcher = control.createMock(TLSEventDispatcher.class);
		sources = control.createMock(TLEventSources.class);
		timeline = new TLSTimeline(cmdQueue, evtQueue, simulation,
				dispatcher, sources);
		evtType = control.createMock(EventType.class);
	}
	
	@Test
	public void testExecute() throws Exception {
		expect(simulation.execute()).andReturn(false);
		expect(simulation.execute()).andReturn(true);
		control.replay();
		
		assertFalse(timeline.execute());
		assertTrue(timeline.execute());
		
		control.verify();
	}
	
	@Test
	public void testGetPOA() throws Exception {
		DateTime time = new DateTime();
		expect(evtQueue.getPOA()).andReturn(time);
		control.replay();
		
		assertSame(time, timeline.getPOA());
		
		control.verify();
	}
	
	@Test
	public void testGetRunInterval() throws Exception {
		expect(evtQueue.getInterval()).andReturn(interval);
		control.replay();
		
		assertSame(interval, timeline.getRunInterval());
		
		control.verify();
	}
	
	@Test
	public void testSchedule2() throws Exception {
		Runnable r = control.createMock(Runnable.class);
		evtQueue.pushEvent(eq(new TLEvent(from, r)));
		control.replay();
		
		timeline.schedule(from, r);
		
		control.verify();
	}
	
	@Test
	public void testSchedule1() throws Exception {
		Runnable r = control.createMock(Runnable.class);
		TLEvent e = new TLEvent(to, r);
		evtQueue.pushEvent(same(e));
		control.replay();
		
		timeline.schedule(e);
		
		control.verify();
	}
	
	@Test
	public void testFireRun() throws Exception {
		dispatcher.fireRun();
		control.replay();
		
		timeline.fireRun();
		
		control.verify();
	}
	
	@Test
	public void testFirePause() throws Exception {
		dispatcher.firePause();
		control.replay();
		
		timeline.firePause();
		
		control.verify();
	}

	@Test
	public void testFireFinish() throws Exception {
		dispatcher.fireFinish();
		control.replay();
		
		timeline.fireFinish();
		
		control.verify();
	}
	
	@Test
	public void testPullCommand_BlockingMode() throws Exception {
		timeline.setBlockingMode(true);
		expect(cmdQueue.pullb()).andReturn(TLCmd.PAUSE);
		control.replay();
		
		assertSame(TLCmd.PAUSE, timeline.pullCommand());
		
		control.verify();
	}
	
	@Test
	public void testPullCommand_NonBlockingMode() throws Exception {
		timeline.setBlockingMode(false);
		expect(cmdQueue.pull()).andReturn(TLCmd.FINISH);
		control.replay();
		
		assertSame(TLCmd.FINISH, timeline.pullCommand());
		
		control.verify();
	}
	
	@Test
	public void testSetBlockingMode() throws Exception {
		assertFalse(timeline.isBlockingMode());
		timeline.setBlockingMode(true);
		assertTrue(timeline.isBlockingMode());
		timeline.setBlockingMode(false);
		assertFalse(timeline.isBlockingMode());
	}
	
	@Test
	public void testSetCutoff() throws Exception {
		// TODO: тест пуша события для целей точного позицирования при останове
		assertNull(timeline.getCutoff());
		timeline.setCutoff(from);
		assertEquals(from, timeline.getCutoff());
		timeline.setCutoff(to);
		assertEquals(to, timeline.getCutoff());
		timeline.setCutoff(null);
		assertNull(timeline.getCutoff());
	}
	
	@Test
	public void testSetState() throws Exception {
		assertNull(timeline.getState());
		timeline.setState(TLCmdType.RUN);
		assertEquals(TLCmdType.RUN, timeline.getState());
	}
	
	@Test
	public void testClose() throws Exception {
		cmdQueue.clear();
		evtQueue.clear();
		sources.close();
		control.replay();
		
		timeline.close();
		
		control.verify();
	}
	
	@Test
	public void testRegisterSource() throws Exception {
		TLEventSource source = control.createMock(TLEventSource.class);
		sources.registerSource(same(source));
		control.replay();
		
		timeline.registerSource(source);
		
		control.verify();
	}
	
	@Test
	public void testRemoveSource() throws Exception {
		TLEventSource source = control.createMock(TLEventSource.class);
		sources.removeSource(same(source));
		control.replay();
		
		timeline.removeSource(source);
		
		control.verify();
	}
	
	@Test
	public void testRunning() throws Exception {
		assertFalse(timeline.running());
		timeline.setState(TLCmdType.PAUSE);
		assertFalse(timeline.running());
		timeline.setState(TLCmdType.RUN);
		assertTrue(timeline.running());
	}
	
	@Test
	public void testPaused() throws Exception {
		assertFalse(timeline.paused());
		timeline.setState(TLCmdType.PAUSE);
		assertTrue(timeline.paused());
	}
	
	@Test
	public void testFinished() throws Exception {
		assertFalse(timeline.finished());
		timeline.setState(TLCmdType.PAUSE);
		assertFalse(timeline.finished());
		timeline.setState(TLCmdType.FINISH);
		assertTrue(timeline.finished());
	}
	
	@Test
	public void testFinish() throws Exception {
		cmdQueue.put(same(TLCmd.FINISH));
		control.replay();
		
		timeline.finish();
		
		control.verify();
	}
	
	@Test
	public void testFinish_SkipIfFinished() throws Exception {
		timeline.setState(TLCmdType.FINISH);
		control.replay();
		
		timeline.finish();
		
		control.verify();
	}

	@Test
	public void testPause() throws Exception {
		timeline.setState(TLCmdType.RUN);
		cmdQueue.put(same(TLCmd.PAUSE));
		control.replay();
		
		timeline.pause();
		
		control.verify();
	}

	@Test
	public void testPause_SkipIfFinished() throws Exception {
		timeline.setState(TLCmdType.FINISH);
		control.replay();
		
		timeline.pause();
		
		control.verify();
	}

	@Test
	public void testRunTo() throws Exception {
		DateTime time = new DateTime(2014, 5, 22, 0, 0, 0, 0);
		cmdQueue.put(new TLCmd(time));
		control.replay();
		
		timeline.runTo(time);
		
		control.verify();
	}
	
	@Test
	public void testRunTo_SkipIfFinished() throws Exception {
		timeline.setState(TLCmdType.FINISH);
		control.replay();
		
		timeline.runTo(from);
		
		control.verify();
	}
	
	@Test
	public void testRun() throws Exception {
		expect(evtQueue.getInterval()).andStubReturn(interval);
		cmdQueue.put(new TLCmd((DateTime) null));
		control.replay();
		
		timeline.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_SkipIfFinished() throws Exception {
		timeline.setState(TLCmdType.FINISH);
		expect(evtQueue.getInterval()).andStubReturn(interval);
		control.replay();
		
		timeline.run();
		
		control.verify();
	}
	
	@Test
	public void testIsCutoff() throws Exception {
		DateTime poa = new DateTime(2014,5,27,15,30,0,0);
		expect(evtQueue.getPOA()).andStubReturn(poa);
		evtQueue.pushEvent((TLEvent)anyObject());
		expectLastCall().anyTimes();
		control.replay();
		
		assertFalse(timeline.isCutoff()); // cutoff is null (not specified)
		
		timeline.setCutoff(new DateTime(2015,1,1,0,0,0,0));
		assertFalse(timeline.isCutoff());

		timeline.setCutoff(new DateTime(2014,5,27,15,30,0,0));
		assertTrue(timeline.isCutoff());

		timeline.setCutoff(new DateTime(2014,5,27,15,29,59,999));
		assertTrue(timeline.isCutoff());
		
		control.verify();
	}
	
	@Test
	public void testIsOutOfInterval_In() throws Exception {
		DateTime poa = new DateTime(2014, 5, 31, 23, 59, 59, 999);
		expect(evtQueue.getPOA()).andStubReturn(poa);
		expect(evtQueue.getInterval()).andStubReturn(interval);
		control.replay();

		assertFalse(timeline.isOutOfInterval());
		
		control.verify();
	}
	
	@Test
	public void testIsOutOfInterval_AtEnd() throws Exception {
		DateTime poa = new DateTime(2014, 6, 1, 0, 0, 0, 0);
		expect(evtQueue.getPOA()).andStubReturn(poa);
		expect(evtQueue.getInterval()).andStubReturn(interval);
		control.replay();

		assertTrue(timeline.isOutOfInterval());
		
		control.verify();
	}
	
	@Test
	public void testIsOutOfInterval_Out() throws Exception {
		DateTime poa = new DateTime(2015, 1, 1, 0, 0, 0, 0);
		expect(evtQueue.getPOA()).andStubReturn(poa);
		expect(evtQueue.getInterval()).andStubReturn(interval);
		control.replay();

		assertTrue(timeline.isOutOfInterval());
		
		control.verify();
	}
	
	@Test
	public void testOnFinish() throws Exception {
		expect(dispatcher.OnFinish()).andReturn(evtType);
		control.replay();
		
		assertSame(evtType, timeline.OnFinish());
		
		control.verify();
	}
	
	@Test
	public void testOnPause() throws Exception {
		expect(dispatcher.OnPause()).andReturn(evtType);
		control.replay();
		
		assertSame(evtType, timeline.OnPause());
		
		control.verify();
	}
	
	@Test
	public void testOnRun() throws Exception {
		expect(dispatcher.OnRun()).andReturn(evtType);
		control.replay();
		
		assertSame(evtType, timeline.OnRun());
		
		control.verify();
	}

	@Test (expected=NoSuchMethodException.class)
	public void testOnStepEventTypeRemoved() throws Exception {
		timeline.getClass().getMethod("OnStep");
	}
	
}
