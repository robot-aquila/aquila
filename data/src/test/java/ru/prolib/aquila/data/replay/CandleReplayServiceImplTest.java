package ru.prolib.aquila.data.replay;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.CandleListener;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.replay.CandleReplayServiceImpl.*;
import ru.prolib.aquila.data.storage.MDStorage;

public class CandleReplayServiceImplTest {
	private static TFSymbol key1, key2, key3;
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		key1 = new TFSymbol(new Symbol("GAZP"), ZTFrame.H1MSK);
		key2 = new TFSymbol(new Symbol("MSFT"), ZTFrame.M10);
		key3 = new TFSymbol(new Symbol("AAPL"), ZTFrame.D1MSK);
	}
	
	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private Scheduler schedulerMock;
	private NodeFactory factoryMock;
	private Lock lockMock;
	private Node nodeMock1, nodeMock2, nodeMock3;
	private SubscrHandler shMock1, shMock2, shMock3;
	private CandleListener listenerMock1, listenerMock2, listenerMock3;
	private Map<TFSymbol, Node> nodes;
	private CandleReplayServiceImpl service;
	private CloseableIterator<Candle> itMock;
	private Set<CandleListener> node_listeners;
	private Lock node_lockMock;
	private AtomicBoolean node_closed;
	private NodeImpl node_service;
	private MDStorage<TFSymbol, Candle> storageMock;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		schedulerMock = control.createMock(Scheduler.class);
		factoryMock = control.createMock(NodeFactory.class);
		lockMock = control.createMock(Lock.class);
		nodeMock1 = control.createMock(Node.class);
		nodeMock2 = control.createMock(Node.class);
		nodeMock3 = control.createMock(Node.class);
		shMock1 = control.createMock(SubscrHandler.class);
		shMock2 = control.createMock(SubscrHandler.class);
		shMock3 = control.createMock(SubscrHandler.class);
		listenerMock1 = control.createMock(CandleListener.class);
		listenerMock2 = control.createMock(CandleListener.class);
		listenerMock3 = control.createMock(CandleListener.class);
		nodes = new HashMap<>();
		service = new CandleReplayServiceImpl(schedulerMock, factoryMock, nodes, lockMock);
		
		// Setup related objects for node testing
		itMock = control.createMock(CloseableIterator.class);
		node_lockMock = control.createMock(Lock.class);
		node_listeners = new LinkedHashSet<>();
		node_closed = new AtomicBoolean(false);
		node_service = new NodeImpl(new Symbol("AAPL"), "foo", itMock, node_listeners, node_lockMock, node_closed);
		
		storageMock = control.createMock(MDStorage.class);
	}
	
	@Test
	public void testSubscribe_NodeExistsNotClosedSubscribed() throws Exception {
		nodes.put(key1, nodeMock1);
		nodes.put(key2, nodeMock2);
		nodes.put(key3, nodeMock3);
		lockMock.lock();
		expect(nodeMock2.closed()).andReturn(false);
		expect(nodeMock2.subscribeIfNotClosed(listenerMock1)).andReturn(shMock1);
		lockMock.unlock();
		control.replay();
		
		SubscrHandler actual =  service.subscribe(key2, listenerMock1);
		
		control.verify();
		assertSame(shMock1, actual);
	}
	
	@Test
	public void testSubscribe_NodeExistsNotClosedButSubscriptionFailed() throws Exception {
		nodes.put(key2, nodeMock2);
		lockMock.lock();
		expect(nodeMock2.closed()).andReturn(false);
		expect(nodeMock2.subscribeIfNotClosed(listenerMock2)).andReturn(null);
		expect(schedulerMock.getCurrentTime()).andReturn(T("2020-01-18T18:26:45Z"));
		expect(factoryMock.produce(key2, T("2020-01-18T18:26:45Z"))).andReturn(nodeMock3);
		expect(nodeMock3.subscribe(listenerMock2)).andReturn(shMock3);
		expect(schedulerMock.schedule(nodeMock3)).andReturn(null);
		lockMock.unlock();
		control.replay();

		SubscrHandler actual = service.subscribe(key2, listenerMock2);
		
		control.verify();
		assertSame(shMock3, actual);
	}
	
	@Test
	public void testSubscribe_NodeExistsButClosed() throws Exception {
		nodes.put(key1, nodeMock1);
		lockMock.lock();
		expect(nodeMock1.closed()).andReturn(true);
		expect(schedulerMock.getCurrentTime()).andReturn(T("2020-01-18T00:00:15Z"));
		expect(factoryMock.produce(key1, T("2020-01-18T00:00:15Z"))).andReturn(nodeMock2);
		expect(nodeMock2.subscribe(listenerMock2)).andReturn(shMock2);
		expect(schedulerMock.schedule(nodeMock2)).andReturn(null); // Does not matter
		lockMock.unlock();
		control.replay();
		
		SubscrHandler actual =  service.subscribe(key1, listenerMock2);
		
		control.verify();
		assertSame(shMock2, actual);
	}
	
	@Test
	public void testSubscribe_NodeNotExists() throws Exception {
		nodes.put(key1, nodeMock1);
		nodes.put(key3, nodeMock3);
		lockMock.lock();
		expect(schedulerMock.getCurrentTime()).andReturn(T("2021-06-27T03:15:27Z"));
		expect(factoryMock.produce(key2, T("2021-06-27T03:15:27Z"))).andReturn(nodeMock2);
		expect(nodeMock2.subscribe(listenerMock2)).andReturn(shMock2);
		expect(schedulerMock.schedule(nodeMock2)).andReturn(null);
		lockMock.unlock();
		control.replay();
		
		SubscrHandler actual = service.subscribe(key2, listenerMock2);
		
		control.verify();
		assertSame(shMock2, actual);
	}
	
	@Test
	public void testSubscribe_FactoryThrownAnError() throws Exception {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Test error");
		lockMock.lock();
		expect(schedulerMock.getCurrentTime()).andReturn(T("2021-06-27T03:15:27Z"));
		expect(factoryMock.produce(key2, T("2021-06-27T03:15:27Z"))).andThrow(new IllegalStateException("Test error"));
		lockMock.unlock();
		control.replay();
		
		service.subscribe(key2, listenerMock3);
	}
	
	@Test
	public void testNodeSubscrHandler_Close() throws Exception {
		NodeSubscrHandler service = new NodeSubscrHandler(nodeMock1, listenerMock1, true);
		nodeMock1.unsubscribe(listenerMock1);
		control.replay();
		
		service.close();
		service.close();
		service.close();
		service.close();
		
		control.verify();
	}
	
	@Test
	public void testNodeImpl_Ctor5() throws Exception {
		assertEquals(new Symbol("AAPL"), node_service.getSymbol());
		assertEquals("foo", node_service.getID());
		assertSame(itMock, node_service.getIterator());
		assertSame(node_listeners, node_service.getListeners());
		assertSame(node_lockMock, node_service.getLock());
		assertSame(node_closed, node_service.getClosed());
	}
	
	@Test
	public void testNodeImpl_Ctor2() throws Exception {
		node_service = new NodeImpl(new Symbol("MOON"), "bar", itMock);
		
		assertEquals(new Symbol("MOON"), node_service.getSymbol());
		assertEquals("bar", node_service.getID());
		assertSame(itMock, node_service.getIterator());
		assertNotNull(node_service.getListeners());
		assertThat(node_service.getListeners(), IsInstanceOf.instanceOf(HashSet.class));
		assertNotNull(node_service.getLock());
		assertThat(node_service.getLock(), IsInstanceOf.instanceOf(ReentrantLock.class));
		assertNotNull(node_service.getClosed());
	}
	
	@Test
	public void testNodeImpl_SubscribeIfNotClosed_ClosedInitially() throws Exception {
		node_closed.set(true);
		control.replay();
		
		SubscrHandler actual = node_service.subscribeIfNotClosed(listenerMock1);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testNodeImpl_SubscribeIfNotClosed_ClosedAfterLocking() throws Exception {
		node_lockMock.lock();
		expectLastCall().andAnswer(() -> { node_closed.set(true); return null; });
		node_lockMock.unlock();
		control.replay();
		
		SubscrHandler actual = node_service.subscribeIfNotClosed(listenerMock1);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testNodeImpl_SubscribeIfNotClosed_SubscribedOK() throws Exception {
		node_lockMock.lock();
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> { assertTrue(node_listeners.contains(listenerMock1)); return null; });
		control.replay();
		
		SubscrHandler actual = node_service.subscribeIfNotClosed(listenerMock1);
		
		control.verify();
		assertTrue(new NodeSubscrHandler(node_service, listenerMock1, true).isEqualTo((NodeSubscrHandler) actual));
	}
	
	@Test
	public void testNodeImpl_Subscribe_ThrowsIfClosed() throws Exception {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Cannot subscribe on closed node");
		node_closed.set(true);
		node_lockMock.lock();
		node_lockMock.unlock();
		control.replay();
		
		node_service.subscribe(listenerMock1);
	}
	
	@Test
	public void testNodeImpl_Subscribe_SubscribedOK() throws Exception {
		node_lockMock.lock();
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> { assertTrue(node_listeners.contains(listenerMock3)); return null; });
		control.replay();
		
		SubscrHandler actual = node_service.subscribe(listenerMock3);
		
		control.verify();
		assertTrue(new NodeSubscrHandler(node_service, listenerMock3, true).isEqualTo((NodeSubscrHandler) actual));
	}
	
	@Test
	public void testNodeImpl_Unsubscribe_OK() throws Exception {
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		node_lockMock.lock();
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> { assertFalse(node_listeners.contains(listenerMock2)); return null; });
		control.replay();
		
		node_service.unsubscribe(listenerMock2);
		
		control.verify();
		assertFalse(node_service.closed());
	}
	
	@Test
	public void testNodeImpl_Unsubscribe_IfLastThenClose() throws Exception {
		node_listeners.add(listenerMock1);
		node_lockMock.lock();
		node_lockMock.lock(); // from #close
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> {
			assertFalse(node_listeners.contains(listenerMock1));
			assertTrue(node_closed.get());
			return null;
		});
		itMock.close();
		node_lockMock.unlock();
		control.replay();
		
		node_service.unsubscribe(listenerMock1);
		
		control.verify();
		assertTrue(node_service.closed());
	}
	
	@Test
	public void testNodeImpl_Unsubscribe_DoNothingIfWasNotSubscribed() throws Exception {
		node_lockMock.lock();
		node_lockMock.unlock();
		control.replay();
		
		node_service.unsubscribe(listenerMock1);
		
		control.verify();
		assertFalse(node_service.closed());
	}
	
	@Test
	public void testNodeImpl_GetNextExecutionTime_OK() throws Exception {
		Candle x = new CandleBuilder()
				.withTimeFrame(ZTFrame.M10)
				.buildCandle("2020-01-18T22:30:00Z", 120L, 121L, 118L, 119L, 1L);
		expect(itMock.next()).andReturn(true);
		expect(itMock.item()).andReturn(x);
		control.replay();
		
		assertEquals(T("2020-01-18T22:40:00Z"), node_service.getNextExecutionTime(null));
		
		control.verify();
	}
	
	@Test
	public void testNodeImpl_GetNextExecutionTime_NoMoreRecords() throws Exception {
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		expect(itMock.next()).andReturn(false);
		node_lockMock.lock();
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> { assertEquals(0, node_listeners.size()); return null; });
		itMock.close();
		control.replay();
		
		assertNull(node_service.getNextExecutionTime(null));
		
		control.verify();
	}
	
	@Test
	public void testNodeImpl_GetNextExecutionTime_ErrorOnNext() throws Exception {
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		expect(itMock.next()).andThrow(new IOException("Test error"));
		node_lockMock.lock();
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> { assertEquals(0, node_listeners.size()); return null; });
		itMock.close();
		control.replay();
		
		assertNull(node_service.getNextExecutionTime(null));
		
		control.verify();
	}
	
	@Test
	public void testNodeImpl_GetNextExecutionTime_ErrorOnItem() throws Exception {
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		expect(itMock.next()).andReturn(true);
		expect(itMock.item()).andThrow(new IOException("Test error"));
		node_lockMock.lock();
		node_lockMock.unlock();
		expectLastCall().andAnswer(() -> { assertEquals(0, node_listeners.size()); return null; });
		itMock.close();
		control.replay();
		
		assertNull(node_service.getNextExecutionTime(null));
		
		control.verify();
	}

	@Test
	public void testNodeImpl_Run_DoNothingIfClosed() throws Exception {
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		node_closed.set(true);
		control.replay();
		
		node_service.run();
		
		control.verify();
	}

	@Test
	public void testNodeImpl_Run_DoNothingIfNoListeners() throws Exception {
		node_lockMock.lock();
		node_lockMock.unlock();
		control.replay();
		
		node_service.run();
		
		control.verify();
	}

	@Test
	public void testNodeImpl_Run_IfGetItemThrows() throws Exception {
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		node_lockMock.lock();
		node_lockMock.unlock();
		expect(itMock.item()).andThrow(new IOException("Test error"));
		control.replay();
		
		node_service.run();
		
		control.verify();
	}
	
	@Test
	public void testNodeImpl_Run_OK() throws Exception {
		Candle x = new CandleBuilder()
				.withTimeFrame(ZTFrame.M10)
				.buildCandle("2020-01-18T22:30:00Z", 120L, 121L, 118L, 119L, 1L);
		node_listeners.add(listenerMock1);
		node_listeners.add(listenerMock2);
		node_listeners.add(listenerMock3);
		node_lockMock.lock();
		node_lockMock.unlock();
		expect(itMock.item()).andReturn(x);
		listenerMock1.onCandle(T("2020-01-18T22:40:00Z"), new Symbol("AAPL"), x);
		listenerMock2.onCandle(T("2020-01-18T22:40:00Z"), new Symbol("AAPL"), x);
		listenerMock3.onCandle(T("2020-01-18T22:40:00Z"), new Symbol("AAPL"), x);
		control.replay();
		
		node_service.run();
		
		control.verify();
	}
	
	@Test
	public void testNodeImpl_IsLongTermTask() throws Exception {
		control.replay();
		
		assertFalse(node_service.isLongTermTask());
		
		control.verify();
	}

	@Test
	public void testNodeFactoryMDS_Produce() throws Exception {
		NodeFactoryMDS service = new NodeFactoryMDS(storageMock);
		expect(storageMock.createReaderFrom(key2, T("1997-12-05T20:30:05Z"))).andReturn(itMock);
		control.replay();
		
		NodeImpl actual = (NodeImpl) service.produce(key2, T("1997-12-05T20:30:05Z"));
		
		control.verify();
		assertEquals(new Symbol("MSFT"), actual.getSymbol());
		assertEquals("MSFT[M10[UTC]]", actual.getID());
		assertSame(itMock, actual.getIterator());
		assertEquals(new HashSet<>(), actual.getListeners());
		assertNotNull(actual.getLock());
		assertNotNull(node_service.getClosed());
	}
	
}
