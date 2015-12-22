package ru.prolib.aquila.probe;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.DataException;
import ru.prolib.aquila.probe.internal.*;
import ru.prolib.aquila.probe.timeline.*;

public class PROBETerminalTest {
	private static final Symbol symbol;
	
	static {
		symbol = new Symbol("foo", "bar", "RUR");
	}
	
	private IMocksControl control;
	private PROBETerminal terminal;
	private Timeline timeline;
	private EventType eventType;
	private Scheduler scheduler;
	private DataProvider dataProvider;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType = control.createMock(EventType.class);
		timeline = control.createMock(Timeline.class);
		scheduler = control.createMock(Scheduler.class);
		dataProvider = control.createMock(DataProvider.class);
		terminal = new PROBETerminalBuilder()
			.withScheduler(scheduler)
			.withTimeline(timeline)
			.withDataProvider(dataProvider)
			.buildTerminal();
		terminal.getEventSystem().getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		EventQueue queue = terminal.getEventSystem().getEventQueue(); 
		queue.stop();
		queue.join(1000);
	}
	
	@Test
	public void testRequestSecurity() throws Exception {
		DateTime start = DateTime.now();
		expect(scheduler.getCurrentTime()).andReturn(start);
		dataProvider.startSupply(terminal, symbol, start);
		control.replay();
		
		terminal.requestSecurity(symbol);
		terminal.requestSecurity(symbol); // ignore repeated requests
		
		control.verify();
	}
	
	@Test
	public void testRequestSecurity_Error() throws Exception {
		final EventType type = terminal.OnRequestSecurityError(); 
		final CountDownLatch finished = new CountDownLatch(1);
		type.addListener(new EventListener() {
			@Override
			public void onEvent(Event actual) {
				assertEquals(new RequestSecurityEvent(type,
						symbol, -1, "Test error"), actual);
				finished.countDown();
			}
		});
		
		DateTime start = DateTime.now();
		expect(scheduler.getCurrentTime()).andReturn(start);
		dataProvider.startSupply(terminal, symbol, start);
		expectLastCall().andThrow(new DataException("Test error"));
		control.replay();
		
		terminal.requestSecurity(symbol);
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testGetRunInterval() throws Exception {
		Interval i = new Interval(DateTime.now(), DateTime.now().plus(1));
		expect(timeline.getRunInterval()).andReturn(i);
		control.replay();
		
		assertEquals(i, terminal.getRunInterval());
		
		control.verify();
	}
	
	@Test
	public void testRunning() throws Exception {
		expect(timeline.running()).andReturn(true);
		expect(timeline.running()).andReturn(false);
		control.replay();
		
		assertTrue(terminal.running());
		assertFalse(terminal.running());
		
		control.verify();
	}
	
	@Test
	public void testPaused() throws Exception {
		expect(timeline.paused()).andReturn(false);
		expect(timeline.paused()).andReturn(true);
		control.replay();
		
		assertFalse(terminal.paused());
		assertTrue(terminal.paused());
		
		control.verify();
	}
	
	@Test
	public void testFinished() throws Exception {
		expect(timeline.finished()).andReturn(true);
		expect(timeline.finished()).andReturn(false);
		control.replay();
		
		assertTrue(terminal.finished());
		assertFalse(terminal.finished());
		
		control.verify();
	}
	
	@Test
	public void testFinish() throws Exception {
		timeline.finish();
		control.replay();
		terminal.setTerminalState(TerminalState.CONNECTED);
		
		terminal.finish();
		
		control.verify();
	}
	
	@Test
	public void testPause() throws Exception {
		timeline.pause();
		control.replay();
		terminal.setTerminalState(TerminalState.CONNECTED);
		
		terminal.pause();
		
		control.verify();
	}
	
	@Test
	public void testRunTo() throws Exception {
		DateTime t = DateTime.now();
		timeline.runTo(t);
		control.replay();
		terminal.setTerminalState(TerminalState.CONNECTED);
		
		terminal.runTo(t);
		
		control.verify();
	}
	
	@Test
	public void testRun() throws Exception {
		timeline.run();
		control.replay();
		terminal.setTerminalState(TerminalState.CONNECTED);
		
		terminal.run();
		
		control.verify();
	}

	@Test
	public void testOnFinish() throws Exception {
		expect(timeline.OnFinish()).andReturn(eventType);
		control.replay();
		
		assertSame(eventType, terminal.OnFinish());
		
		control.verify();
	}

	@Test
	public void testOnPause() throws Exception {
		expect(timeline.OnPause()).andReturn(eventType);
		control.replay();
		
		assertSame(eventType, terminal.OnPause());
		
		control.verify();
	}

	@Test
	public void testOnRun() throws Exception {
		expect(timeline.OnRun()).andReturn(eventType);
		control.replay();
		
		assertSame(eventType, terminal.OnRun());
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testRun_ThrowsIfNotConnected() {
		terminal.run();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testRunTo_ThrowsIfNotConnected() {
		terminal.runTo(DateTime.now());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testPause_ThrowsIfNotConnected() {
		terminal.pause();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testFinish_ThrowsIfNotConnected() {
		terminal.finish();
	}
	
	@Ignore
	@Test
	public void testMarkTerminalConnected() {
		// skipped, too complex
		fail("TODO: incomplete");
	}
	
	@Test
	public void testMarkTerminalConnected_SkipsIfFinished() {
		expect(timeline.finished()).andReturn(true);
		control.replay();
		terminal.setTerminalState(TerminalState.CONNECTED);
		
		terminal.markTerminalConnected();
		
		control.verify();
	}
	
	@Ignore
	@Test
	public void testMarkTerminalDisconnected() throws Exception {
		fail("TODO: incomplete");
	}

}
