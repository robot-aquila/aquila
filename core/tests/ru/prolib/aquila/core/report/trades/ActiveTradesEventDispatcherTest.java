package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

public class ActiveTradesEventDispatcherTest {
	private IMocksControl control;
	private RTrade report;
	private EventListener listener;
	private ActiveTradesEventDispatcher dispatcher;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		report = control.createMock(RTrade.class);
		listener = control.createMock(EventListener.class);
		dispatcher = new ActiveTradesEventDispatcher();
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed =
			new EventDispatcherImpl(new SimpleEventQueue(), "ActiveTrades");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnEnter(), ed.createType("Enter"));
		assertEquals(dispatcher.OnExit(), ed.createType("Exit"));
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
	}
	
	@Test
	public void testFireEnter() throws Exception {
		dispatcher.OnEnter().addListener(listener);
		listener.onEvent(eq(new TradeReportEvent(dispatcher.OnEnter(), report)));
		control.replay();
		
		dispatcher.fireEnter(report);
		
		control.verify();
	}

	@Test
	public void testFireExit() throws Exception {
		dispatcher.OnExit().addListener(listener);
		listener.onEvent(eq(new TradeReportEvent(dispatcher.OnExit(), report)));
		control.replay();
		
		dispatcher.fireExit(report);
		
		control.verify();
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		listener.onEvent(eq(new TradeReportEvent(dispatcher.OnChanged(), report)));
		control.replay();
		
		dispatcher.fireChanged(report);
		
		control.verify();
	}

}
