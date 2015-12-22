package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

public class ActiveTradesEventDispatcherTest {
	private EventSystem es;
	private IMocksControl control;
	private RTrade report;
	private EventListener listener;
	private ActiveTradesEventDispatcher dispatcher;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		control = createStrictControl();
		report = control.createMock(RTrade.class);
		listener = control.createMock(EventListener.class);
		dispatcher = new ActiveTradesEventDispatcher(es);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals("ActiveTrades", ed.getId());

		EventType type;
		type = dispatcher.OnEnter();
		assertEquals("ActiveTrades.Enter", type.getId());
		assertTrue(type.isOnlySyncMode());
		
		type = dispatcher.OnExit();
		assertEquals("ActiveTrades.Exit", type.getId());
		assertTrue(type.isOnlySyncMode());
		
		type = dispatcher.OnChanged();
		assertEquals("ActiveTrades.Changed", type.getId());
		assertTrue(type.isOnlySyncMode());
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
