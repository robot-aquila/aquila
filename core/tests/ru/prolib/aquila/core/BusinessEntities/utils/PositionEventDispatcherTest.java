package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PositionEventDispatcherTest {
	private static SecurityDescriptor descr;
	private static Account account;
	private IMocksControl control;
	private Position position;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private PositionEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RI", "SPBFUT", "USD", SecurityType.FUT);
		account = new Account("foo", "bar");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		position = control.createMock(Position.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new PositionEventDispatcher(es, account, descr);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed =
			es.createEventDispatcher("Position[foo#bar:RI@SPBFUT(FUT/USD)]");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
	}
	
	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new PositionEvent(dispatcher.OnChanged(), position)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireChanged(position);
		
		control.verify();
	}

}
