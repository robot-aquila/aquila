package ru.prolib.aquila.core.eqs.v4;

import static org.easymock.EasyMock.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.concurrency.SelectiveBarrier;
import ru.prolib.aquila.core.eque.EventDispatchingRequest;

public class V4QueueTest {
	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private BlockingQueue<EventDispatchingRequest> basicMock;
	private SelectiveBarrier barrierMock;
	private EventDispatchingRequest reqStub = EventDispatchingRequest.EXIT;
	private V4Queue service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicMock = control.createMock(BlockingQueue.class);
		barrierMock = control.createMock(SelectiveBarrier.class);
		service = new V4Queue(basicMock, barrierMock, 1000L);
	}
	
	@Test
	public void testStart() {
		control.replay();
		
		service.start();
		
		control.verify();
	}
	
	@Test
	public void testWaitForFlushing() throws Exception {
		barrierMock.setAllowAll(false);
		basicMock.put(EventDispatchingRequest.FLUSH);
		barrierMock.await(5000L, TimeUnit.MILLISECONDS);
		control.replay();
		
		service.waitForFlushing(5000L, TimeUnit.MILLISECONDS);
		
		control.verify();
	}
	
	@Test
	public void testPut() throws Exception {
		barrierMock.await(1000L, TimeUnit.MILLISECONDS);
		basicMock.put(reqStub);
		control.replay();
		
		service.put(reqStub);
		
		control.verify();
	}
	
	@Test
	public void testPut_IfBarrierThrows() throws Exception {
		eex.expect(IllegalStateException.class);
		barrierMock.await(1000L, TimeUnit.MILLISECONDS);
		expectLastCall().andThrow(new TimeoutException());
		control.replay();
		
		service.put(reqStub);
	}
	
	@Test
	public void testTake_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.take();
	}
	
	@Test
	public void testPoll0_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.poll();
	}
	
	@Test
	public void testElement_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.element();
	}
	
	@Test
	public void testPeek_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.peek();
	}
	
	@Test
	public void testRemove_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.remove();
	}
	
	@Test
	public void testAddAll_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.addAll(null);
	}
	
	@Test
	public void testClear_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.clear();
	}
	
	@Test
	public void testContainsAll_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.containsAll(null);
	}
	
	@Test
	public void testIsEmpty_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.isEmpty();
	}
	
	@Test
	public void testIterator_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.iterator();
	}
	
	@Test
	public void testRemoveAll_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.removeAll(null);
	}
	
	@Test
	public void testRetainAll_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.retainAll(null);
	}

	@Test
	public void testSize_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.size();
	}
	
	@Test
	public void testToArray0_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.toArray();
	}
	
	@Test
	public void testToArray1_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.toArray((Object[])null);
	}

	@Test
	public void testAdd_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.add(reqStub);
	}
	
	@Test
	public void testContains_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.contains(reqStub);
	}
	
	@Test
	public void testDrainTo1_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.drainTo(null);
	}
	
	@Test
	public void testDrainTo2_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.drainTo(null, 0);
	}
	
	@Test
	public void testOffer1_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.offer(reqStub);
	}
	
	@Test
	public void testOffer3_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.offer(reqStub, 1L, TimeUnit.SECONDS);
	}
	
	@Test
	public void testPoll1_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.poll(98L, TimeUnit.MICROSECONDS);
	}
	
	@Test
	public void testRemainingCapacity_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.remainingCapacity();
	}
	
	@Test
	public void testRemove1_Unsupported() throws Exception {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.remove(reqStub);
	}

}
