package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PositionEventDispatcherTest {
	private static Symbol symbol;
	private static Account account;
	private IMocksControl control;
	private Position position;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private PositionEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol = new Symbol("RI", "SPBFUT", "USD", SymbolType.FUTURE);
		account = new Account("foo", "bar");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		position = control.createMock(Position.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new PositionEventDispatcher(es, account, symbol);
	}
	
	@Test
	public void testStructure() throws Exception {
		String did = "Position[foo#bar:F:RI@SPBFUT:USD]";
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals(did, ed.getId());
		
		EventType type;
		type = dispatcher.OnChanged();
		assertEquals(did + ".Changed", type.getId());
		assertFalse(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new PositionEvent(dispatcher.OnChanged(), position)));
		control.replay();
		
		dispatcher.fireChanged(position);
		
		control.verify();
	}

}
