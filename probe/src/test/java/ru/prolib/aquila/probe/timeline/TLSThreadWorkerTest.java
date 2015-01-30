package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import java.util.concurrent.CountDownLatch;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.sm.SMStateMachine;

public class TLSThreadWorkerTest {
	private IMocksControl control;
	private CountDownLatch started;
	private TLSTimeline timeline;
	private SMStateMachine automat;
	private TLSThreadWorker worker;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		started = control.createMock(CountDownLatch.class);
		timeline = control.createMock(TLSTimeline.class);
		automat = control.createMock(SMStateMachine.class);
		worker = new TLSThreadWorker(started, timeline, automat);
		
	}

	@Test
	public void testRun() throws Exception {
		TLCmd cmd;
		automat.start();
		started.countDown();
		// pass 1
		expect(timeline.finished()).andReturn(false);
		cmd = new TLCmd(new DateTime());
		expect(timeline.pullCommand()).andReturn(cmd);
		automat.input(same(cmd));
		// pass 2
		expect(timeline.finished()).andReturn(false);
		cmd = new TLCmd(new DateTime(2014,5,22,10,0,0,0));
		expect(timeline.pullCommand()).andReturn(cmd);
		automat.input(same(cmd));
		// pass 3
		expect(timeline.finished()).andReturn(false);
		expect(timeline.pullCommand()).andReturn(TLCmd.FINISH);
		automat.input(same(TLCmd.FINISH));
		// pass 4 (final)
		expect(timeline.finished()).andReturn(true);
		control.replay();
		
		worker.run();
		
		control.verify();
	}
	
	@Test
	public void testSetDebug() throws Exception {
		automat.setDebug(eq(true));
		automat.setDebug(eq(false));
		control.replay();
		
		worker.setDebug(true);
		worker.setDebug(false);
		
		control.verify();
	}

}
