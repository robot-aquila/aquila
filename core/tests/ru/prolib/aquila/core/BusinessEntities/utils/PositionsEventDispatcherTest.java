package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PositionsEventDispatcherTest {
	private static Account account;
	private IMocksControl control;
	private Position position;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private PositionsEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("foo", "bar");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		position = control.createMock(Position.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new PositionsEventDispatcher(es, account);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = es.createEventDispatcher("Positions[foo#bar]");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
		assertEquals(dispatcher.OnAvailable(), ed.createType("Available"));
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		dispatcher.OnAvailable().addListener(listener);
		queue.enqueue(eq(new PositionEvent(dispatcher.OnAvailable(), position)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireAvailable(position);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent() throws Exception {
		EventType type = control.createMock(EventType.class);
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new PositionEvent(dispatcher.OnChanged(), position)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new PositionEvent(type, position));
		
		control.verify();
	}
	
}
