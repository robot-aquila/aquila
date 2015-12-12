package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class SecurityEventDispatcherTest {
	private static Symbol symbol;
	private IMocksControl control;
	private Security security;
	private Trade trade;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private SecurityEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol = new Symbol("RI", "SPBFUT", "USD", SymbolType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		security = control.createMock(Security.class);
		trade = control.createMock(Trade.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new SecurityEventDispatcher(es, symbol);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals("Security[RI@SPBFUT(FUT/USD)]", ed.getId());

		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnChanged();
		assertEquals("Security[RI@SPBFUT(FUT/USD)].Changed", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnTrade();
		assertEquals("Security[RI@SPBFUT(FUT/USD)].Trade", type.getId());
		assertFalse(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new SecurityEvent((EventTypeSI) dispatcher.OnChanged(), security)));
		control.replay();
		
		dispatcher.fireChanged(security);
		
		control.verify();
	}
	
	@Test
	public void testFireTrade() throws Exception {
		dispatcher.OnTrade().addListener(listener);
		queue.enqueue(eq(new SecurityTradeEvent((EventTypeSI) dispatcher.OnTrade(), security, trade)));
		control.replay();
		
		dispatcher.fireTrade(security, trade);
		
		control.verify();
	}

}
