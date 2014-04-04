package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

public class TLInterrogationStrategyTest {
	private static final DateTime poa = new DateTime(2013, 1, 1, 0, 0, 15, 0);
	private IMocksControl control;
	private TLEventSources sources;
	private TLEventSource source;
	private TLEventQueue queue;
	private TLInterrogationStrategy phases;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sources = control.createMock(TLEventSources.class);
		source = control.createMock(TLEventSource.class);
		queue = control.createMock(TLEventQueue.class);
		phases = new TLInterrogationStrategy(sources, queue);
		
		expect(queue.getPOA()).andStubReturn(poa);
	}
	
	@Test
	public void testInterrogate_RemoveClosedSources() throws Exception {
		expect(source.closed()).andReturn(true);
		sources.removeSource(same(source));
		control.replay();
		
		phases.interrogate(source);
		
		control.verify();
	}
	
	@Test
	public void testInterrogate_PresentEvent() throws Exception {
		TLEvent event = new TLEvent(poa, null);
		expect(source.closed()).andReturn(false);
		expect(source.pullEvent()).andReturn(event);
		queue.pushEvent(same(event));
		control.replay();
		
		phases.interrogate(source);
		
		control.verify();
	}
	
	@Test
	public void testInterrogate_FutureEvent() throws Exception {
		TLEvent event = new TLEvent(poa.plus(237), null);
		expect(source.closed()).andReturn(false);
		expect(source.pullEvent()).andReturn(event);
		queue.pushEvent(same(event));
		sources.disableUntil(same(source), eq(poa.plus(237)));
		control.replay();
		
		phases.interrogate(source);
		
		control.verify();
	}
	
	@Test
	public void testInterrogate_NoEvent() throws Exception {
		expect(source.closed()).andReturn(false);
		expect(source.pullEvent()).andReturn(null);
		sources.disableUntil(same(source), eq(poa.plus(1)));
		control.replay();

		phases.interrogate(source);

		control.verify();
	}
	
	@Test
	public void testInterrogate_CatchExceptionAtPull() throws Exception {
		expect(source.closed()).andReturn(false);
		expect(source.pullEvent()).andThrow(new TLException("test error"));
		sources.removeSource(same(source));
		control.replay();
		
		phases.interrogate(source);
		
		control.verify();
	}
	
	@Test
	public void testGetForInterrogating() throws Exception {
		List<TLEventSource> list = new Vector<TLEventSource>();
		expect(sources.getSources(eq(poa))).andReturn(list);
		control.replay();
		
		assertSame(list, phases.getForInterrogating());
		
		control.verify();
	}

}
