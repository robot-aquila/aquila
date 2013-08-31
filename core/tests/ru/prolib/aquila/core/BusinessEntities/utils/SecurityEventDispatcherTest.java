package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class SecurityEventDispatcherTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private Security security;
	private Trade trade;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private SecurityEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RI", "SPBFUT", "USD", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		security = control.createMock(Security.class);
		trade = control.createMock(Trade.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new SecurityEventDispatcher(es, descr);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed =
			es.createEventDispatcher("Security[RI@SPBFUT(FUT/USD)]");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
		assertEquals(dispatcher.OnTrade(), ed.createType("Trade"));
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new SecurityEvent(dispatcher.OnChanged(), security)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireChanged(security);
		
		control.verify();
	}
	
	@Test
	public void testFireTrade() throws Exception {
		dispatcher.OnTrade().addListener(listener);
		queue.enqueue(eq(new SecurityTradeEvent(dispatcher.OnTrade(),
				security, trade)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireTrade(security, trade);
		
		control.verify();
	}

}
