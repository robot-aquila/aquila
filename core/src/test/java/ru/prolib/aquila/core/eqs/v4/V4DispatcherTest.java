package ru.prolib.aquila.core.eqs.v4;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.concurrency.SelectiveBarrier;
import ru.prolib.aquila.core.eque.EventDispatchingRequest;
import ru.prolib.aquila.core.eque.EventQueueService;

public class V4DispatcherTest {
	
	static class TestEvent extends EventImpl {
		private final int round;

		public TestEvent(EventType type, int round) {
			super(type);
			this.round = round;
		}
		
		@Override
		public String toString() {
			return "TestEvent#" + round;
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(1154907, 7)
					.append(round)
					.build();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != TestEvent.class ) {
				return false;
			}
			TestEvent o = (TestEvent) other;
			return o.round == round;
		}
		
	}
	
	static class TestEventFactory implements EventFactory {
		private final int round;
		
		public TestEventFactory(int round) {
			this.round = round;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new TestEvent(type, round);
		}
		
	}
	
	private IMocksControl control;
	private BlockingQueue<EventDispatchingRequest> queueMock;
	private SelectiveBarrier barrierMock;
	private EventQueueService eqsMock;
	private EventListener listenerMock;
	private V4Dispatcher service;
	private EventType typeStub1, typeStub2;
	private EventListenerStub listenerStub1, listenerStub2;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(BlockingQueue.class);
		barrierMock = control.createMock(SelectiveBarrier.class);
		eqsMock = control.createMock(EventQueueService.class);
		listenerMock = control.createMock(EventListener.class);
		service = new V4Dispatcher(queueMock, barrierMock, eqsMock);
		typeStub1 = new EventTypeImpl();
		typeStub2 = new EventTypeImpl();
		listenerStub1 = new EventListenerStub();
		listenerStub2 = new EventListenerStub();
	}
	
	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testRun() throws Exception {
		typeStub1.addListener(listenerMock);
		typeStub1.addListener(listenerStub1);
		typeStub2.addListener(listenerStub2);
		typeStub1.addAlternateType(typeStub2);
		barrierMock.setAllowedThread(Thread.currentThread());
		// element#0 - regular event (take)
		expect(queueMock.take()).andReturn(new EventDispatchingRequest(typeStub1, new TestEventFactory(1)));
		listenerMock.onEvent(new TestEvent(typeStub1, 1));
		eqsMock.eventDispatched(anyLong(), anyLong());
		// element#1 - await flushing
		expect(queueMock.take()).andReturn(EventDispatchingRequest.FLUSH);
		// element#2 - regular event (poll)
		expect(queueMock.poll()).andReturn(new EventDispatchingRequest(typeStub1, new TestEventFactory(2)));
		listenerMock.onEvent(new TestEvent(typeStub1, 2));
		eqsMock.eventDispatched(anyLong(), anyLong());
		// element#3 - regular event (poll)
		expect(queueMock.poll()).andReturn(new EventDispatchingRequest(typeStub1, new TestEventFactory(3)));
		listenerMock.onEvent(new TestEvent(typeStub1, 3));
		eqsMock.eventDispatched(anyLong(), anyLong());
		// flushing
		expect(queueMock.poll()).andReturn(null);
		barrierMock.setAllowAll(true);
		// element#4 - regular event (take)
		expect(queueMock.take()).andReturn(new EventDispatchingRequest(typeStub1, new TestEventFactory(4)));
		listenerMock.onEvent(new TestEvent(typeStub1, 4));
		eqsMock.eventDispatched(anyLong(), anyLong());
		// element#5 - exit
		expect(queueMock.take()).andReturn(EventDispatchingRequest.EXIT);
		eqsMock.shutdown();
		control.replay();
		
		service.run();
		
		control.verify();
		assertEquals(new TestEvent(typeStub1, 1), listenerStub1.getEvent(0));
		assertEquals(new TestEvent(typeStub1, 2), listenerStub1.getEvent(1));
		assertEquals(new TestEvent(typeStub1, 3), listenerStub1.getEvent(2));
		assertEquals(new TestEvent(typeStub1, 4), listenerStub1.getEvent(3));
		assertEquals(new TestEvent(typeStub2, 1), listenerStub2.getEvent(0));
		assertEquals(new TestEvent(typeStub2, 2), listenerStub2.getEvent(1));
		assertEquals(new TestEvent(typeStub2, 3), listenerStub2.getEvent(2));
		assertEquals(new TestEvent(typeStub2, 4), listenerStub2.getEvent(3));
	}

}
