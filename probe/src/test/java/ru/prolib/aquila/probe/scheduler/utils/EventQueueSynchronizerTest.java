package ru.prolib.aquila.probe.scheduler.utils;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.probe.scheduler.utils.EventQueueSynchronizer.*;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class EventQueueSynchronizerTest {
	private IMocksControl control;
	private EventQueue queueMock;
	private EventType type;
	private EventQueueSynchronizer service;

	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		type = new EventTypeImpl();
		service = new EventQueueSynchronizer(queueMock, type, Long.MIN_VALUE, Long.MIN_VALUE);
	}
	
	@Test
	public void testCtor() {
		assertTrue(type.isListener(service));
		assertEquals(Long.MIN_VALUE, service.getLastID());
		assertEquals(Long.MIN_VALUE, service.getLastSentID());
	}
	
	@Test
	public void testClose() {
		service.close();
		
		assertFalse(type.isListener(service));
	}
	
	@Test
	public void testAfterExecution() {
		queueMock.enqueue(type, new SynchronizeEventFactory(Long.MIN_VALUE + 1));
		control.replay();
		
		service.afterExecution(null);
		
		control.verify();
		assertEquals(Long.MIN_VALUE + 1, service.getLastSentID());
		assertEquals(Long.MIN_VALUE, service.getLastID());
	}
	
	@Test
	public void testAfterExecution_SkipIfCLosed() {
		service.close();
		control.replay();
		
		service.afterExecution(null);
		
		control.verify();
		assertEquals(Long.MIN_VALUE, service.getLastSentID());
		assertEquals(Long.MIN_VALUE, service.getLastID());		
	}
	
	@Test
	public void testAfterExecution_ID_Overflow() {
		service = new EventQueueSynchronizer(queueMock, type, Long.MAX_VALUE, Long.MAX_VALUE);
		queueMock.enqueue(type, new SynchronizeEventFactory(Long.MIN_VALUE));
		control.replay();
		
		service.afterExecution(null);
		
		control.verify();
		assertEquals(Long.MIN_VALUE, service.getLastSentID());
		assertEquals(Long.MAX_VALUE, service.getLastID());
	}
	
	@Test
	public void testWaitForThread() throws Exception {
		service = new EventQueueSynchronizer(queueMock, type, 1L, 2L);
		CountDownLatch finished = new CountDownLatch(1);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					service.waitForThread(null);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				finished.countDown();
			}
		};
		t.start();
		service.onEvent(new SynchronizeEvent(type, 2L));
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(2L, service.getLastID());
		assertEquals(2L, service.getLastSentID());
	}
	
	@Test
	public void testWaitForThread_SkipIfNotCounted() throws Exception {
		service = new EventQueueSynchronizer(queueMock, type, 1L, 1L);
		CountDownLatch finished = new CountDownLatch(1);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					service.waitForThread(null);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				finished.countDown();
			}
		};
		t.start();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testWaitForThread_SkipIfClosed() throws Exception {
		service = new EventQueueSynchronizer(queueMock, type, 1L, 2L);
		service.close();
		CountDownLatch finished = new CountDownLatch(1);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					service.waitForThread(null);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				finished.countDown();
			}
		};
		t.start();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testWaitForThread_ID_Overflowing() throws Exception {
		service = new EventQueueSynchronizer(queueMock, type, Long.MAX_VALUE, Long.MIN_VALUE);
		CountDownLatch finished = new CountDownLatch(1);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					service.waitForThread(null);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				finished.countDown();
			}
		};
		t.start();
		service.onEvent(new SynchronizeEvent(type, Long.MIN_VALUE));
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(Long.MIN_VALUE, service.getLastID());
		assertEquals(Long.MIN_VALUE, service.getLastSentID());
	}

}
