package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueFactory;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.DataProvider;

public class TerminalParamsTest {
	private Scheduler scheduler;
	private IMocksControl control;
	private DataProvider dataProvider;
	private ObjectFactory objectFactory;
	private EventQueue queue;
	private EventDispatcher dispatcherMock;
	private TerminalParams params;
	Lock lockMock;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		scheduler = control.createMock(Scheduler.class);
		dataProvider = control.createMock(DataProvider.class);
		objectFactory = control.createMock(ObjectFactory.class);
		dispatcherMock = control.createMock(EventDispatcher.class);
		lockMock = control.createMock(Lock.class);
		queue = new EventQueueFactory().createDefault();
		params = new TerminalParams();
	}

	@Test
	public void testGettersAndSetters() {
		params.setScheduler(scheduler);
		params.setEventQueue(queue);
		params.setDataProvider(dataProvider);
		params.setTerminalID("DummyTerminal");
		params.setObjectFactory(objectFactory);
		params.setEventDispatcher(dispatcherMock);
		params.setLock(lockMock);
		
		assertSame(scheduler, params.getScheduler());
		assertSame(queue, params.getEventQueue());
		assertSame(dataProvider, params.getDataProvider());
		assertEquals("DummyTerminal", params.getTerminalID());
		assertSame(objectFactory, params.getObjectFactory());
		assertSame(dispatcherMock, params.getEventDispatcher());
		assertSame(lockMock, params.getLock());
	}
	
	@Test
	public void testGetScheduler_DefaultInstance() {
		SchedulerLocal scheduler = (SchedulerLocal) params.getScheduler();
		assertNotNull(scheduler);
		assertSame(scheduler, params.getScheduler());
	}
	
	@Test
	public void testGetEventQueue_DefaultInstance() {
		EventQueueImpl queue = (EventQueueImpl) params.getEventQueue();
		assertNotNull(queue);
		assertSame(queue, params.getEventQueue());
	}
	
	@Test
	public void testGetObjectFactory_DefaultInstance() {
		params.setLock(lockMock);
		
		ObjectFactoryImpl factory = (ObjectFactoryImpl) params.getObjectFactory();
		
		assertNotNull(factory);
		assertSame(factory, params.getObjectFactory());
		
		// So, object factory now must use a global lock which shared
		// across all objects of terminal and terminal itself.
		assertEquals(new ObjectFactoryImpl(lockMock), factory);
	}
	
	@Test
	public void testGetTerminalID_Auto() throws Exception {
		int baseNumber = TerminalParams.getCurrentGeneratedNumber();
		String expected1 = "Terminal#" + (baseNumber + 1),
				expected2 = "Terminal#" + (baseNumber + 2);
		
		assertEquals(expected1, params.getTerminalID());
		assertEquals(expected2, params.getTerminalID());
	}
	
	@Test
	public void testGetEventDispatcher_DefaultInstance() throws Exception {
		params.setEventQueue(queue);
		
		EventDispatcherImpl dispatcher = (EventDispatcherImpl) params.getEventDispatcher();
		
		assertNotNull(dispatcher);
		assertSame(queue, dispatcher.getEventQueue());
	}
	
	@Test
	public void testGetLock_DefaultInstance() throws Exception {
		Lock lock = params.getLock();
		
		assertNotNull(lock);
		assertThat(lock, IsInstanceOf.instanceOf(ReentrantLock.class));
	}

}
