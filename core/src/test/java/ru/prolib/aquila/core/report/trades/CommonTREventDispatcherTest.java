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
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals("Report", ed.getId());
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnEnter();
		assertEquals("Report.Enter", type.getId());
		assertTrue(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnExit();
		assertEquals("Report.Exit", type.getId());
		assertTrue(type.isOnlySyncMode());

		type = (EventTypeSI) dispatcher.OnChanged();
		assertEquals("Report.Changed", type.getId());
		assertTrue(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireEnter() throws Exception {
		dispatcher.OnEnter().addListener(listener);
		queue.enqueue(eq(new TradeReportEvent((EventTypeSI) dispatcher.OnEnter(), report, 8)));
		control.replay();
		
		dispatcher.fireEnter(report, 8);
		
		control.verify();
	}
	
	@Test
	public void testFireExit() throws Exception {
		dispatcher.OnExit().addListener(listener);
		queue.enqueue(eq(new TradeReportEvent((EventTypeSI) dispatcher.OnExit(), report, 12)));
		control.replay();
		
		dispatcher.fireExit(report, 12);
		
		control.verify();
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new TradeReportEvent((EventTypeSI) dispatcher.OnChanged(), report, 5)));
		control.replay();
		
		dispatcher.fireChanged(report, 5);
		
		control.verify();
	}

}
