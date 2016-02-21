package ru.prolib.aquila.datatools.tickdatabase;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.datatools.tickdatabase.SimpleL1Replay.ConsumeTickTask;
import ru.prolib.aquila.datatools.tickdatabase.SimpleL1Replay.ReaderFactory;

public class SimpleL1ReplayTest {
	private static final TickType TRADE = TickType.TRADE;
	private static final List<L1Update> FIXTURE_UPDATES1;
	
	static {
		List<L1Update> list = new ArrayList<L1Update>();
		list.add(U("MSFT", TRADE, "2016-02-18T19:39:16.922Z", 52.5272d, 35L));
		list.add(U("CHKP", TRADE, "2016-02-18T19:39:16.937Z", 81.77d, 100L));
		list.add(U("CSCO", TRADE, "2016-02-18T19:39:17.003Z", 26.445d, 100L));
		list.add(U("CSCO", TRADE, "2016-02-18T19:39:17.003Z", 26.45d, 100L));
		list.add(U("CHRW", TRADE, "2016-02-18T19:39:17.016Z", 71.08d, 100L));
		list.add(U("CHRW", TRADE, "2016-02-18T19:39:17.016Z", 71.08d, 100L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 100L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 100L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 1L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 100L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 100L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 100L));
		list.add(U("DISH", TRADE, "2016-02-18T19:39:16.991Z", 42.26d, 100L));
		list.add(U("SYMC", TRADE, "2016-02-18T19:39:17.059Z", 19.84d, 100L));
		list.add(U("AAPL", TRADE, "2016-02-18T19:39:17.144Z", 96.9465d, 100L));
		list.add(U("NFLX", TRADE, "2016-02-18T19:39:17.247Z", 91.7d, 20L));
		list.add(U("SIRI", TRADE, "2016-02-18T19:39:17.244Z", 3.56d, 100L));
		list.add(U("TSCO", TRADE, "2016-02-18T19:39:17.329Z", 85.1, 100L));
		list.add(U("FB",   TRADE, "2016-02-18T19:39:17.431Z", 104.29d, 100L));
		list.add(U("FB",   TRADE, "2016-02-18T19:39:17.431Z", 104.29d, 100L));
		list.add(U("FB",   TRADE, "2016-02-18T19:39:17.431Z", 104.29d, 67L));
		list.add(U("FB",   TRADE, "2016-02-18T19:39:17.431Z", 104.29d, 76L));
		list.add(U("FB",   TRADE, "2016-02-18T19:39:17.432Z", 104.29d, 100L));
		list.add(U("YHOO", TRADE, "2016-02-18T19:39:17.387Z", 29.59d, 100L));
		list.add(U("NFLX", TRADE, "2016-02-18T19:39:17.531Z", 91.72d, 1L));
		list.add(U("FB",   TRADE, "2016-02-18T19:39:17.622Z", 104.29d, 200L));
		list.add(U("QCOM", TRADE, "2016-02-18T19:39:17.637Z", 49.11d, 100L));
		FIXTURE_UPDATES1 = list;
	}

	/**
	 * Shortcut to create update.
	 * <p>
	 * @param symbol - symbol string
	 * @param type - tick type
	 * @param timestamp - timestamp string
	 * @param price - price
	 * @param size - size
	 * @return L1 update instance
	 */
	private static L1Update U(String symbol, TickType type,
			String timestamp, double price, long size)
	{
		return new L1UpdateImpl(new Symbol(symbol),
				Tick.of(type, Instant.parse(timestamp), price, size));
	}
	
	static class L1UpdateReaderStub implements L1UpdateReader, L1UpdateConsumer {
		private final ArrayList<L1Update> updates;
		private boolean closed = false;
		
		public L1UpdateReaderStub() {
			updates = new ArrayList<L1Update>();
		}
		
		public boolean isClosed() {
			return closed;
		}

		@Override
		public void close() throws IOException {
			updates.clear();
			closed = true;
		}

		@Override
		public boolean nextUpdate() throws IOException {
			return updates.size() > 0;
		}

		@Override
		public L1Update getUpdate() throws IOException {
			return updates.remove(0);
		}
		
		public int getSize() {
			return updates.size();
		}
				
		public L1Update addUpdate(L1Update update) {
			updates.add(update);
			return update;
		}

		@Override
		public void consume(L1Update update) {
			addUpdate(update);
		}
		
	}
	
	static class L1UpdateConsumerStub implements L1UpdateConsumer {
		private final ArrayList<L1Update> updates;
		
		public L1UpdateConsumerStub() {
			updates = new ArrayList<L1Update>();
		}
		
		@Override
		public void consume(L1Update update) {
			updates.add(update);
		}
		
		public int getSize() {
			return updates.size();
		}
		
		public L1Update get(int index) {
			return updates.get(index);
		}
		
		public void close() {
			updates.clear();
		}
		
		public List<L1Update> getConsumedUpdates() {
			return updates;
		}
		
	}
	
	static class SchedulerTaskStub implements TaskHandler, Comparable<SchedulerTaskStub> {
		private Instant timestamp;
		private Runnable runnable;
		
		public SchedulerTaskStub(Instant timestamp, Runnable runnable) {
			this.timestamp = timestamp;
			this.runnable = runnable;
		}
		
		public Instant getTimestamp() {
			return timestamp;
		}
		
		public Runnable getRunnable() {
			return runnable;
		}

		@Override
		public boolean cancel() {
			return false;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != SchedulerTaskStub.class ) {
				return false;
			}
			SchedulerTaskStub o = (SchedulerTaskStub) other;
			return new EqualsBuilder()
				.append(timestamp, o.timestamp)
				.append(runnable, o.runnable)
				.isEquals();
		}

		@Override
		public int compareTo(SchedulerTaskStub other) {
			return timestamp.compareTo(other.timestamp);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()
					+ "[time=" + timestamp + ", " + runnable + "]";
		}
		
	}
	
	static class SchedulerStub implements Scheduler {
		private Instant current = Instant.EPOCH;
		private final Map<Instant, List<SchedulerTaskStub>> tasks;
		
		public SchedulerStub() {
			super();
			tasks = new HashMap<Instant, List<SchedulerTaskStub>>();
		}
		
		public List<SchedulerTaskStub> getTasks() {
			List<SchedulerTaskStub> result = new ArrayList<SchedulerTaskStub>();
			List<Instant> timeList = new ArrayList<Instant>(tasks.keySet());
			Collections.sort(timeList);
			for ( Instant time : timeList ) {
				result.addAll(tasks.get(time));
			}
			return result;
		}
		
		public void clearTasks() {
			tasks.clear();
		}
		
		public int getSize() {
			return getTasks().size();
		}
		
		public void setCurrentTime(Instant instant) {
			this.current = instant;
		}

		@Override
		public Instant getCurrentTime() {
			return current;
		}

		@Override
		public TaskHandler schedule(Runnable task, Instant time) {
			SchedulerTaskStub h = new SchedulerTaskStub(time, task);
			List<SchedulerTaskStub> dummyList = tasks.get(time);
			if ( dummyList == null ) {
				dummyList = new ArrayList<SchedulerTaskStub>();
				tasks.put(time, dummyList);
			}
			dummyList.add(h);
			return h;
		}

		@Override
		public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public TaskHandler schedule(Runnable task, long delay) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public TaskHandler schedule(Runnable task, long delay, long period) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public TaskHandler scheduleAtFixedRate(Runnable task, Instant firstTime, long period) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public TaskHandler scheduleAtFixedRate(Runnable task, long delay, long period) {
			throw new RuntimeException("Not implemented");
		}

		@Override
		public void close() {
			tasks.clear();
		}
	
	}
	
	private IMocksControl control;
	private ReaderFactory readerFactoryMock;
	private L1UpdateReaderStub updateReaderStub;
	private L1UpdateConsumerStub consumerStub;
	private SchedulerStub schedulerStub;
	private SimpleL1Replay replay;
	private File file = new File("foo/bar.csv");
	
	/**
	 * Shortcut to create update tick task.
	 * <p>
	 * @param timePlanned - planned time string
	 * @param update - initial update instance (the time will be changed)
	 * @param sequenceID - sequence ID
	 * @return task handler
	 */
	private SchedulerTaskStub UT(String timePlanned, L1Update update, long sequenceID) {
		Instant newTime = Instant.parse(timePlanned);
		Tick oldTick = update.getTick();
		L1Update newUpdate = new L1UpdateImpl(update.getSymbol(), Tick.of(oldTick.getType(),
				newTime, oldTick.getPrice(), oldTick.getSize()));
		return new SchedulerTaskStub(newTime,
				new SimpleL1Replay.ConsumeTickTask(replay, newUpdate, sequenceID));
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		schedulerStub = new SchedulerStub();
		consumerStub = new L1UpdateConsumerStub();
		readerFactoryMock = control.createMock(ReaderFactory.class);
		updateReaderStub = new L1UpdateReaderStub();
		replay = new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock, 3, 5);
	}
	
	@After
	public void tearDown() throws Exception {
		schedulerStub.close();
		consumerStub.close();
		updateReaderStub.close();
	}
	
	private void loadUpdates(List<L1Update> updates, L1UpdateConsumer consumer) {
		for ( L1Update update : updates ) {
			consumer.consume(update);
		}
	}
	
	@Test
	public void testCtor5() throws Exception {
		replay = new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock, 3, 6);
		assertSame(schedulerStub, replay.getScheduler());
		assertSame(consumerStub, replay.getConsumer());
		assertSame(readerFactoryMock, replay.getReaderFactory());
		assertEquals(3, replay.getMinQueueSize());
		assertEquals(6, replay.getMaxQueueSize());
	}
	
	@Test
	public void testCtor3() throws Exception {
		replay = new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock);
		assertSame(schedulerStub, replay.getScheduler());
		assertSame(consumerStub, replay.getConsumer());
		assertSame(readerFactoryMock, replay.getReaderFactory());
		assertEquals(100, replay.getMinQueueSize());
		assertEquals(200, replay.getMaxQueueSize());
	}
	
	@Test
	public void testCtor2() throws Exception {
		replay = new SimpleL1Replay(schedulerStub, consumerStub);
		assertSame(schedulerStub, replay.getScheduler());
		assertSame(consumerStub, replay.getConsumer());
		assertEquals(SimpleL1Replay.SimpleCsvL1ReaderFactory.class, replay.getReaderFactory().getClass());
		assertEquals(100, replay.getMinQueueSize());
		assertEquals(200, replay.getMaxQueueSize());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor5_ThrowsInvalidMinQueueSize() throws Exception {
		new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock, 0, 10);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor5_ThrowsInvalidMaxQueueSize() throws Exception {
		new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock, 5, 5);
	}
	
	@Test
	public void testStartReadingUpdates_InitialLoad() throws Exception {
		replay = new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock, 10, 20);
		loadUpdates(FIXTURE_UPDATES1, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		
		replay.startReadingUpdates(file);
		
		control.verify();
		assertTrue(replay.isStarted());
		List<L1Update> list = FIXTURE_UPDATES1;
		List<SchedulerTaskStub> expected = new ArrayList<SchedulerTaskStub>(),
				actual = schedulerStub.getTasks();
		expected.add(UT("1970-01-01T00:00:00.000Z", list.get(0), 1));
		expected.add(UT("1970-01-01T00:00:00.015Z", list.get(1), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(6), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(7), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(8), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(9), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(10), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(11), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(12), 1));
		expected.add(UT("1970-01-01T00:00:00.081Z", list.get(2), 1));
		expected.add(UT("1970-01-01T00:00:00.081Z", list.get(3), 1));
		expected.add(UT("1970-01-01T00:00:00.094Z", list.get(4), 1));
		expected.add(UT("1970-01-01T00:00:00.094Z", list.get(5), 1));
		expected.add(UT("1970-01-01T00:00:00.137Z", list.get(13), 1));
		expected.add(UT("1970-01-01T00:00:00.222Z", list.get(14), 1));
		expected.add(UT("1970-01-01T00:00:00.322Z", list.get(16), 1));
		expected.add(UT("1970-01-01T00:00:00.325Z", list.get(15), 1));
		expected.add(UT("1970-01-01T00:00:00.407Z", list.get(17), 1));
		// Considering all data the #23 should be next.
		// But the queue is limited up to 20 elements.
		// So the next is #18.
		expected.add(UT("1970-01-01T00:00:00.509Z", list.get(18), 1));
		expected.add(UT("1970-01-01T00:00:00.509Z", list.get(19), 1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testStartReadingUpdates_EndOfData() throws Exception {
		replay = new SimpleL1Replay(schedulerStub, consumerStub, readerFactoryMock, 10, 20);
		List<L1Update> list = FIXTURE_UPDATES1.subList(0, 10);
		loadUpdates(list, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		
		replay.startReadingUpdates(file);
		
		control.verify();
		assertTrue(replay.isStarted());
		assertTrue(updateReaderStub.isClosed());
		assertEquals(10, schedulerStub.getSize());
	}
	
	@Test
	public void testStartReadingUpdates_EndOfData_DoNotStopWhenTaskScheduled()
		throws Exception
	{
		List<L1Update> list = FIXTURE_UPDATES1.subList(0, 3);
		loadUpdates(list, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		
		replay.startReadingUpdates(file);
		
		control.verify();
		assertTrue(replay.isStarted());
		assertTrue(updateReaderStub.isClosed());
		List<SchedulerTaskStub> scheduled = schedulerStub.getTasks();
		
		scheduled.get(0).getRunnable().run();
		
		assertTrue(replay.isStarted());
		
		scheduled.get(1).getRunnable().run();
		
		assertTrue(replay.isStarted());
		
		scheduled.get(2).getRunnable().run();
		
		assertFalse(replay.isStarted());
	}
	
	@Test
	public void testConsumeUpdate() throws Exception {
		List<L1Update> list = FIXTURE_UPDATES1;
		loadUpdates(list, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		replay.startReadingUpdates(file);
		control.reset();
		
		replay.consumeUpdate(list.get(0), 1);
		replay.consumeUpdate(list.get(1), 1);
		replay.consumeUpdate(list.get(2), 1);
		
		List<L1Update> expected = new ArrayList<L1Update>();
		expected.add(list.get(0));
		expected.add(list.get(1));
		expected.add(list.get(2));
		assertEquals(expected, consumerStub.getConsumedUpdates());
	}
	
	@Test
	public void testConsumeUpdate_FillUpQueue() throws Exception {
		List<L1Update> list = FIXTURE_UPDATES1;
		loadUpdates(list, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		// Should schedule first five updates: 0,1,2,3,4
		replay.startReadingUpdates(file);
		control.reset();
		schedulerStub.clearTasks();
		
		replay.consumeUpdate(list.get(0), 1); // queued 4
		assertEquals(0, schedulerStub.getSize());
		
		replay.consumeUpdate(list.get(1), 1); // queued 3
		assertEquals(0, schedulerStub.getSize());
		
		replay.consumeUpdate(list.get(2), 1); // queued 2, it's time to fill up
		assertEquals(3, schedulerStub.getSize());
		
		List<SchedulerTaskStub> expected = new ArrayList<SchedulerTaskStub>(),
				actual = schedulerStub.getTasks();
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(6), 1));
		expected.add(UT("1970-01-01T00:00:00.069Z", list.get(7), 1));
		expected.add(UT("1970-01-01T00:00:00.094Z", list.get(5), 1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConsumeUpdate_SkipObsoleteUpdates() throws Exception {
		List<L1Update> list = FIXTURE_UPDATES1;
		expect(readerFactoryMock.createReader(file)).andStubReturn(updateReaderStub);
		control.replay();
		replay.startReadingUpdates(file); // sequenceID=1
		replay.startReadingUpdates(file); // sequenceID=2
		loadUpdates(list, updateReaderStub);
		replay.startReadingUpdates(file); // sequenceID=3
		control.reset();
		
		replay.consumeUpdate(list.get(0), 1); // should be skipped
		replay.consumeUpdate(list.get(1), 2); // should be skipped
		replay.consumeUpdate(list.get(2), 3); // should be consumed
		replay.consumeUpdate(list.get(3), 4); // should be skipped
		
		List<L1Update> expected = new ArrayList<L1Update>();
		expected.add(list.get(2));
		assertEquals(expected, consumerStub.getConsumedUpdates());
	}
	
	@Test
	public void testConsumeUpdate_SkipIfNotStarted() throws Exception {
		replay.consumeUpdate(FIXTURE_UPDATES1.get(0), 1);
		replay.consumeUpdate(FIXTURE_UPDATES1.get(0), 0);

		assertEquals(0, consumerStub.getSize());
		assertEquals(0, schedulerStub.getSize());
	}
	
	@Test
	public void testClose() throws Exception {
		loadUpdates(FIXTURE_UPDATES1, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		replay.startReadingUpdates(file);
		control.reset();
		assertTrue(replay.isStarted());
		
		replay.close();

		assertTrue(updateReaderStub.isClosed());
		assertFalse(replay.isStarted());
	}
	
	@Test
	public void testStopReadingUpdates() throws Exception {
		loadUpdates(FIXTURE_UPDATES1, updateReaderStub);
		expect(readerFactoryMock.createReader(file)).andReturn(updateReaderStub);
		control.replay();
		replay.startReadingUpdates(file);
		control.reset();
		assertTrue(replay.isStarted());
		
		replay.stopReadingUpdates();

		assertTrue(updateReaderStub.isClosed());
		assertFalse(replay.isStarted());
	}
	
	@Test
	public void testConsumeTickTask() throws Exception {
		SimpleL1Replay replayMock = control.createMock(SimpleL1Replay.class);
		L1Update expectedUpdate = FIXTURE_UPDATES1.get(5);
		replayMock.consumeUpdate(expectedUpdate, 120);
		control.replay();
		
		ConsumeTickTask task = new ConsumeTickTask(replayMock, expectedUpdate, 120);
		task.run();
		
		control.verify();
	}

}
