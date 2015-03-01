package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PortfolioEventDispatcherTest {
	private static Account account;
	private IMocksControl control;
	private Portfolio portfolio;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private PortfolioEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("foo", "bar");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		portfolio = control.createMock(Portfolio.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new PortfolioEventDispatcher(es, account);
	}
	
	@Test
	public void testStructure() throws Exception {
		String did = "Portfolio[foo#bar]";
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals(did, ed.getId());
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnChanged();
		assertEquals(did + ".Changed", type.getId());
		assertFalse(type.isOnlySyncMode());
	}

	@Test
	public void testFireChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new PortfolioEvent((EventTypeSI) dispatcher.OnChanged(), portfolio)));
		control.replay();
		
		dispatcher.fireChanged(portfolio);
		
		control.verify();
	}

}
