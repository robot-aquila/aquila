package ru.prolib.aquila.data.replay;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStub;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStubTask;
import ru.prolib.aquila.core.BusinessEntities.TStamped;

public class TStampedReplayTest {
	
	/**
	 * Node to test the replay class.
	 */
	static class Node implements TStamped {
		private final Instant time;
		private final int customProperty;
		
		public Node(Instant time, int customProperty) {
			this.time = time;
			this.customProperty = customProperty;
		}
		
		public Node(String timeString, int customProperty) {
			this(Instant.parse(timeString), customProperty);
		}
		
		public int getCustomProperty() {
			return customProperty;
		}

		@Override
		public Instant getTime() {
			return time;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != Node.class ) {
				return false;
			}
			Node o = (Node) other;
			return new EqualsBuilder()
				.append(time, o.time)
				.append(customProperty, o.customProperty)
				.isEquals();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + time + " custProp:" + customProperty + "]";
		}
		
	}
	
	/**
	 * Service to test the replay class.
	 */
	static class NodeReplayService implements TStampedReplayService {
		private final List<Node> consumed;
		private final List<CloseableIterator<Node>> readers;
		private Duration timeDiff;
		
		public NodeReplayService() {
			consumed = new ArrayList<>();
			readers = new ArrayList<>();
		}
		
		/**
		 * Create a new reader stub as an answer for the {@link #createReader()} call.
		 * <p>
		 * @return the new reader stub
		 */
		public CloseableIteratorStub<Node> newReaderResponse() {
			CloseableIteratorStub<Node> iterator = new CloseableIteratorStub<Node>();
			readers.add(iterator);
			return iterator;
		}
		
		public List<Node> getConsumedNodes() {
			return consumed;
		}
		
		public void clearConsumedNodes() {
			consumed.clear();
		}

		@Override
		public CloseableIterator<? extends TStamped> createReader() throws IOException {
			if ( readers.size() > 0 ) {
				timeDiff = null;
				return readers.remove(0);
			}
			throw new IOException("No more readers");
		}

		@Override
		public Instant consumptionTime(Instant currentTime, TStamped object) {
			Instant objectTime = object.getTime();
			if ( timeDiff == null ) {
				timeDiff = Duration.between(objectTime, currentTime);
				return currentTime;
			} else {
				return objectTime.plus(timeDiff);
			}
		}

		@Override
		public TStamped mutate(TStamped object, Instant consumptionTime) {
			return new Node(consumptionTime, ((Node) object).customProperty);
		}

		@Override
		public void consume(TStamped object) throws IOException {
			consumed.add((Node) object);
		}
		
	}
	
	private static Node newNode(String timeString, int customProperty) {
		return new Node(timeString, customProperty);
	}
	
	private static final List<Node> FIXTURE;
	static {
		FIXTURE = new ArrayList<>();
		FIXTURE.add(newNode("2016-02-18T19:39:16.922Z",  1));
		FIXTURE.add(newNode("2016-02-18T19:39:16.937Z",  2));
		FIXTURE.add(newNode("2016-02-18T19:39:17.003Z",  3));
		FIXTURE.add(newNode("2016-02-18T19:39:16.991Z",  7));
		FIXTURE.add(newNode("2016-02-18T19:39:17.059Z", 14));
		FIXTURE.add(newNode("2016-02-18T19:39:16.992Z", 13));
		FIXTURE.add(newNode("2016-02-18T19:39:17.144Z", 15));
		FIXTURE.add(newNode("2016-02-18T19:39:17.247Z", 16));
		FIXTURE.add(newNode("2016-02-18T19:39:17.244Z", 17));
		FIXTURE.add(newNode("2016-02-18T19:39:17.329Z", 18));
		FIXTURE.add(newNode("2016-02-18T19:39:17.431Z", 19));
		FIXTURE.add(newNode("2016-02-18T19:39:17.431Z", 20));
		FIXTURE.add(newNode("2016-02-18T19:39:17.431Z", 21));
		FIXTURE.add(newNode("2016-02-18T19:39:17.431Z", 22));
		FIXTURE.add(newNode("2016-02-18T19:39:17.432Z", 23));
		FIXTURE.add(newNode("2016-02-18T19:39:17.387Z", 24));
		FIXTURE.add(newNode("2016-02-18T19:39:17.531Z", 25));
		FIXTURE.add(newNode("2016-02-18T19:39:17.622Z", 26));
		FIXTURE.add(newNode("2016-02-18T19:39:17.637Z", 27));
	}
	
	private IMocksControl control;
	private EventQueue eventQueue;
	private SchedulerStub schedulerStub;
	private NodeReplayService serviceStub;
	private EventListenerStub listenerStub;
	private TStampedReplay replay;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventQueue = new EventQueueImpl();
		schedulerStub = new SchedulerStub();
		serviceStub = new NodeReplayService();
		listenerStub = new EventListenerStub();
		replay = new TStampedReplay(eventQueue, schedulerStub, serviceStub, "zulu24", 3, 5);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	private CloseableIteratorStub<Node> newReaderResponse(List<Node> nodes) {
		CloseableIteratorStub<Node> iterator = serviceStub.newReaderResponse();
		for ( Node node : nodes ) {
			iterator.add(node);
		}
		return iterator;
	}
	
	/**
	 * Create a new scheduler task to compare to.
	 * <p>
	 * Note: The replay must be started to get an assigned sequenceID.
	 * <p>
	 * @param node - the node
	 * @return scheduler task
	 */
	private SchedulerStubTask newTask(Node node) {
		return SchedulerStubTask.atTime(node.getTime(),
			new TStampedReplayConsume(replay, replay.getSequenceID(), node));
	}
	
	@Test
	public void test_Node_Equals() {
		Node node1 = newNode("2016-08-01T00:10:00Z", 5),
			node2 = newNode("2016-08-01T00:10:00Z", 5),
			node3 = newNode("2000-01-01T00:00:00Z", 5),
			node4 = newNode("2016-08-01T00:10:00Z", 8);
		assertTrue(node1.equals(node2));
		assertFalse(node1.equals(node3));
		assertFalse(node1.equals(node4));
		assertFalse(node1.equals(null));
		assertFalse(node1.equals(this));
	}
	
	@Test
	public void test_Consume_Equals() {
		Node object = newNode("2016-08-01T01:31:00Z", 5);
		TStampedReplayConsume consume1 = new TStampedReplayConsume(replay, 10, object),
				consume2 = new TStampedReplayConsume(replay, 10, object),
				consume3 = new TStampedReplayConsume(control.createMock(TStampedReplay.class), 10, object),
				consume4 = new TStampedReplayConsume(replay, 10, newNode("2000-01-01T00:00:00Z", 5)),
				consume5 = new TStampedReplayConsume(replay, 5, object);
		assertTrue(consume1.equals(consume2));
		assertFalse(consume1.equals(consume3));
		assertFalse(consume1.equals(consume4));
		assertFalse(consume1.equals(consume5));
		assertFalse(consume1.equals(null));
		assertFalse(consume1.equals(this));
	}
	
	@Test
	public void test_Consume_Run() throws Exception {
		Node node = newNode("2016-08-01T02:02:00Z", 5);
		TStampedReplay replayMock = control.createMock(TStampedReplay.class);
		replayMock.consume(8, node);
		control.replay();
		TStampedReplayConsume consume = new TStampedReplayConsume(replayMock, 8, node);
		
		consume.run();
		
		control.verify();
	}
	
	@Test
	public void testCtor6() {
		assertSame(eventQueue, replay.getEventQueue());
		assertSame(schedulerStub, replay.getScheduler());
		assertSame(serviceStub, replay.getService());
		assertEquals("zulu24", replay.getServiceID());
		assertEquals(3, replay.getMinQueueSize());
		assertEquals(5, replay.getMaxQueueSize());
		assertEquals("zulu24.STARTED", replay.onStarted().getId());
		assertEquals("zulu24.STOPPED", replay.onStopped().getId());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor6_ThrowsInvalidMinQueueSize() throws Exception {
		new TStampedReplay(eventQueue, schedulerStub, serviceStub, "zulu24", 0, 10).close();
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor6_ThrowsInvalidMaxQueueSize() throws Exception {
		new TStampedReplay(eventQueue, schedulerStub, serviceStub, "zulu24", 5, 5).close();
	}
	
	@Test
	public void testCtor5() {
		replay = new TStampedReplay(eventQueue, schedulerStub, "foobar", 10, 20);
		assertSame(eventQueue, replay.getEventQueue());
		assertSame(schedulerStub, replay.getScheduler());
		assertNull(replay.getService());
		assertEquals("foobar", replay.getServiceID());
		assertEquals(10, replay.getMinQueueSize());
		assertEquals(20, replay.getMaxQueueSize());
		assertEquals("foobar.STARTED", replay.onStarted().getId());
		assertEquals("foobar.STOPPED", replay.onStopped().getId());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor5_ThrowsInvalidMinQueueSize() throws Exception {
		new TStampedReplay(eventQueue, schedulerStub, "zulu24", 0, 10).close();
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor5_ThrowsInvalidMaxQueueSize() throws Exception {
		new TStampedReplay(eventQueue, schedulerStub, "zulu24", 5, 5).close();
	}
	
	@Test
	public void testSetService() {
		TStampedReplayService serviceMock = control.createMock(TStampedReplayService.class);
		replay.setService(serviceMock);
		
		assertSame(serviceMock, replay.getService());
	}
	
	@Test
	public void testIsReady() {
		assertTrue(replay.isReady());
		replay.setService(null);
		assertFalse(replay.isReady());
	}
	
	@Test
	public void testStart_QueueFullyFilled() throws Exception {
		newReaderResponse(FIXTURE);
		schedulerStub.setFixedTime(Instant.EPOCH.plusMillis(922L));
		replay.onStarted().addSyncListener(listenerStub);
		replay.onStopped().addSyncListener(listenerStub);
		
		replay.start();

		assertTrue(replay.isStarted());
		// At this phase check that expected tasks have been and scheduled.
		// Those nodes not consumed yet by service.
		List<SchedulerStubTask> actual = schedulerStub.getScheduledTasks();
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(newTask(newNode("1970-01-01T00:00:00.922Z",  1)));
		expected.add(newTask(newNode("1970-01-01T00:00:00.937Z",  2)));
		expected.add(newTask(newNode("1970-01-01T00:00:00.991Z",  7)));
		// Considering all data the #6 should be next.
		// But the queue is limited up to 5 elements.
		// So the next is #3.
		expected.add(newTask(newNode("1970-01-01T00:00:01.003Z",  3)));
		expected.add(newTask(newNode("1970-01-01T00:00:01.059Z", 14)));		
		assertEquals(expected, actual);
		
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(replay.onStarted()));
	}
	
	@Test
	public void testStart_NotEnoughElementsToFillQueue() throws Exception {
		newReaderResponse(FIXTURE.subList(0, 3));
		schedulerStub.setFixedTime(Instant.EPOCH.plusMillis(922L));
		
		replay.start();

		assertTrue(replay.isStarted());
		List<SchedulerStubTask> actual = schedulerStub.getScheduledTasks();
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(newTask(newNode("1970-01-01T00:00:00.922Z",  1)));
		expected.add(newTask(newNode("1970-01-01T00:00:00.937Z",  2)));
		expected.add(newTask(newNode("1970-01-01T00:00:01.003Z",  3)));
		assertEquals(expected, actual);
	}	
	
	@Test
	public void testStart_ThrowsIfStarted() throws Exception {
		newReaderResponse(FIXTURE);
		
		replay.start();
		try {
			replay.start();
			fail("Expected exception: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("zulu24: Already started", e.getMessage());
		}
	}
	
	@Test
	public void testConsume_JustOneObject() throws Exception {
		newReaderResponse(FIXTURE);
		schedulerStub.setFixedTime(Instant.EPOCH.plusMillis(922L));
		replay.start();
		replay.onStopped().addSyncListener(listenerStub);
		Node node = newNode("2017-12-31T00:00:00Z", 9);
		
		replay.consume(replay.getSequenceID(), node);
		
		List<Node> expectedNodes = new ArrayList<>();
		expectedNodes.add(node);
		assertEquals(expectedNodes, serviceStub.getConsumedNodes());
		
		// Must not be stopped
		assertEquals(0, listenerStub.getEventCount());
	}

	@Test
	public void testConsume_QueueFilled() throws Exception {
		newReaderResponse(FIXTURE);
		schedulerStub.setFixedTime(Instant.EPOCH.plusMillis(922L));
		replay.start();
		replay.onStopped().addSyncListener(listenerStub);
		
		List<SchedulerStubTask> scheduled = schedulerStub.getScheduledTasks();
		schedulerStub.clearScheduledTasks();
		scheduled.get(0).getRunnable().run(); // 4 in queue, wait
		scheduled.get(1).getRunnable().run(); // 3 in queue, wait
		scheduled.get(2).getRunnable().run(); // 2 in queue, fill queue
		
		// Nodes ##5-7 must be scheduled for execution
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(newTask(newNode("1970-01-01T00:00:00.992Z", 13)));
		expected.add(newTask(newNode("1970-01-01T00:00:01.144Z", 15)));
		expected.add(newTask(newNode("1970-01-01T00:00:01.247Z", 16)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		
		// Nodes ##0-3 must be consumed
		List<Node> expectedNodes = new ArrayList<>();
		expectedNodes.add(newNode("1970-01-01T00:00:00.922Z",  1));
		expectedNodes.add(newNode("1970-01-01T00:00:00.937Z",  2));
		expectedNodes.add(newNode("1970-01-01T00:00:00.991Z",  7));
		assertEquals(expectedNodes, serviceStub.getConsumedNodes());
		
		// Must not be stopped
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test
	public void testConsume_NotEnoughElementsToFillQueue() throws Exception {
		CloseableIteratorStub<Node> it = newReaderResponse(FIXTURE.subList(0, 7));
		schedulerStub.setFixedTime(Instant.EPOCH.plusMillis(922));
		replay.start();
		replay.onStopped().addSyncListener(listenerStub);

		List<SchedulerStubTask> scheduled = schedulerStub.getScheduledTasks();
		schedulerStub.clearScheduledTasks();
		scheduled.get(0).getRunnable().run(); // 4 in queue, wait
		scheduled.get(1).getRunnable().run(); // 3 in queue, wait
		scheduled.get(2).getRunnable().run(); // 2 in queue, fill queue
		
		// Nodes ##5-6 must be scheduled for execution
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(newTask(newNode("1970-01-01T00:00:00.992Z", 13)));
		expected.add(newTask(newNode("1970-01-01T00:00:01.144Z", 15)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		
		// Must not be stopped
		assertEquals(0, listenerStub.getEventCount());
		assertTrue(it.isClosed());
	}
	
	@Test
	public void testConsume_StopAtLastElement() throws Exception {
		newReaderResponse(FIXTURE.subList(0, 3));
		schedulerStub.setFixedTime(Instant.EPOCH.plusMillis(922));
		replay.start();
		replay.onStopped().addSyncListener(listenerStub);

		List<SchedulerStubTask> scheduled = schedulerStub.getScheduledTasks();
		scheduled.get(0).getRunnable().run();
		
		assertTrue(replay.isStarted());
		
		scheduled.get(1).getRunnable().run();
		
		assertTrue(replay.isStarted());
		
		scheduled.get(2).getRunnable().run();
		
		assertFalse(replay.isStarted());
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(replay.onStopped()));
	}
	
	@Test
	public void testConsume_SkipObsoleteElement() throws Exception {
		newReaderResponse(FIXTURE.subList(0, 5));
		newReaderResponse(FIXTURE.subList(5, 9));
		replay.start(); // sequenceID=1
		replay.stop();
		replay.start(); // sequenceID=2
		
		Node node1 = newNode("2017-12-31T00:00:00Z", 9),
			node2 = newNode("2017-12-31T00:10:30Z", 8),
			node3 = newNode("2017-12-31T00:10:35Z", 7);
		
		replay.consume(2, node1);
		replay.consume(1, node2);
		replay.consume(2, node3);
		
		List<Node> expectedNodes = new ArrayList<>();
		expectedNodes.add(node1);
		expectedNodes.add(node3);
		assertEquals(expectedNodes, serviceStub.getConsumedNodes());
	}

	@Test
	public void testConsume_SkipIfNotStarted() throws Exception {
		newReaderResponse(FIXTURE.subList(0, 5));
		replay.start();
		replay.stop();
		
		Node node1 = newNode("2017-12-31T00:00:00Z", 9);
			
		replay.consume(1, node1);
			
		List<Node> expectedNodes = new ArrayList<>();
		assertEquals(expectedNodes, serviceStub.getConsumedNodes());
	}
	
	@Test
	public void testStop() throws Exception {
		CloseableIteratorStub<Node> it = newReaderResponse(FIXTURE);
		replay.start();
		replay.onStopped().addSyncListener(listenerStub);
		
		replay.stop();
		
		assertFalse(replay.isStarted());
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(replay.onStopped()));
		assertTrue(it.isClosed());
	}

	@Test
	public void testClose() throws Exception {
		EventType type = new EventTypeImpl();
		CloseableIteratorStub<Node> it = newReaderResponse(FIXTURE);
		replay.start();
		replay.onStarted().addAlternateType(type);
		replay.onStopped().addAlternateType(type);
		replay.onStarted().addSyncListener(listenerStub);
		replay.onStopped().addSyncListener(listenerStub);

		replay.close();
		
		assertFalse(replay.isStarted());
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(replay.onStopped()));
		assertTrue(it.isClosed());
		assertFalse(replay.onStarted().isAlternateType(type));
		assertFalse(replay.onStopped().isAlternateType(type));
		assertFalse(replay.onStarted().isListener(listenerStub));
		assertFalse(replay.onStopped().isListener(listenerStub));
	}

}
