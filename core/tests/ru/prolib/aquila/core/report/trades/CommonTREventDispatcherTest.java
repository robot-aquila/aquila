package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

public class CommonTREventDispatcherTest {
	private IMocksControl control;
	private RTrade report;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private CommonTREventDispatcher dispatcher;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		report = control.createMock(RTrade.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new CommonTREventDispatcher(es);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = es.createEventDispatcher("Report");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnEnter(), ed.createType("Enter"));
		assertEquals(dispatcher.OnExit(), ed.createType("Exit"));
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
	}
	
	@Test
	public void testFireEnter() throws Exception {
		dispatcher.OnEnter().addListener(listener);
		queue.enqueue(eq(new TradeReportEvent(dispatcher.OnEnter(),
				report, 8)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireEnter(report, 8);
		
		control.verify();
	}
	
	@Test
	public void testFireExit() throws Exception {
		dispatcher.OnExit().addListener(listener);
		queue.enqueue(eq(new TradeReportEvent(dispatcher.OnExit(),
				report, 12)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireExit(report, 12);
		
		control.verify();
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new TradeReportEvent(dispatcher.OnChanged(),
				report, 5)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireChanged(report, 5);
		
		control.verify();
	}

}
