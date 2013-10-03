package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class DataSourceEventTranslatorTest {

	private static IMocksControl control;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	
	private Event source;
	private EventType onOccur;
	private EventDispatcher dispatcher;
	
	private DataSourceEventTranslator evt;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();		
		
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void SetUp() throws Exception {
		control = createStrictControl();
		dispatcher = eventSystem.createEventDispatcher();
		source = control.createMock(Event.class);
		onOccur = dispatcher.createType();
		
		evt = new DataSourceEventTranslator(dispatcher, onOccur);
		queue.start();
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testOnEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final EventTranslatorEvent expected = new EventTranslatorEvent(
				onOccur, source);
		evt.OnEventOccur().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertEquals(expected, e);
				finished.countDown();
				
			}			
		});
		evt.onEvent(source);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testConstructor() {
		assertEquals(dispatcher, evt.getDispatcher());
		assertEquals(onOccur, evt.OnEventOccur());
	}

}
