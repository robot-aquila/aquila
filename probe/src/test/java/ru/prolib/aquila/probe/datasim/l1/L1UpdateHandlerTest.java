package ru.prolib.aquila.probe.datasim.l1;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStub;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStubTask;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class L1UpdateHandlerTest {
	private static final Symbol symbol = new Symbol("GAZP");
	
	static class UpdateReaderFactoryStub implements L1UpdateReaderFactory {
		private CloseableIterator<L1Update> predefinedReader;

		@Override
		public CloseableIterator<L1Update>
			createReader(Symbol symbol, Instant startTime) throws IOException {
			return predefinedReader;
		}
		
	}
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private SchedulerStub schedulerStub;
	private L1UpdateReaderFactory readerFactoryMock;
	private UpdateReaderFactoryStub readerFactoryStub;
	private L1UpdateConsumer consumerMock1, consumerMock2;
	private CloseableIterator<L1Update> readerStub;
	private L1UpdateHandler handler;
	private Set<L1UpdateConsumer> consumersStub;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		consumerMock1 = control.createMock(L1UpdateConsumer.class);
		consumerMock2 = control.createMock(L1UpdateConsumer.class);
		readerFactoryMock = control.createMock(L1UpdateReaderFactory.class);
		schedulerStub = new SchedulerStub();
		schedulerStub.setFixedTime(T("2016-10-18T02:30:00Z"));
		readerFactoryStub = new UpdateReaderFactoryStub();
		consumersStub = new LinkedHashSet<>();
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryStub, consumersStub);
	}
	
	@After
	public void tearDown() throws Exception {
		if ( readerStub != null ) {
			readerStub.close();
			readerStub = null;
		}
	}
	
	private List<L1Update> getDefaultInput() {
		List<L1Update> input = new ArrayList<>();
		input.add(new L1UpdateBuilder(symbol)
			.withTime("2016-10-18T10:00:00Z")
			.withTrade()
			.withPrice(120.19d)
			.withSize(800L)
			.buildL1Update());
		return input;
	}
	
	private void useDefaultInput() {
		readerFactoryStub.predefinedReader = new CloseableIteratorStub<>(getDefaultInput());
	}
	
	private void assertHandlerSequenceIsObsolete(int sequenceID) {
		assertNotEquals(sequenceID, handler.getCurrentSequenceID());
	}
	
	private void assertHandlerSequenceIsStillActive(int sequenceID) {
		assertEquals(sequenceID, handler.getCurrentSequenceID());
	}
	
	@Test
	public void testSubscribe_ScheduleUpdateAtFirstConsumer() throws Exception {
		List<L1Update> input = getDefaultInput();
		readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		control.replay();
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock);
		
		handler.subscribe(consumerMock1);
		
		control.verify();
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("2016-10-18T10:00:00Z"),
				new L1UpdateTask(input.get(0), 1, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertHandlerSequenceIsStillActive(1);
	}
	
	@Test
	public void testSubscribe_SkipConsumerDuplicates() throws Exception {
		useDefaultInput();
		
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		handler.subscribe(consumerMock1);
		
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock1.consume(task.getUpdate());
		consumerMock2.consume(task.getUpdate());
		control.replay();
		handler.consume(task.getUpdate(), 1);
		control.verify();
	}
	
	@Test
	public void testSubscribe_FinishSequenceOnError() throws Exception {
		@SuppressWarnings("unchecked")
		CloseableIterator<L1Update> readerMock = control.createMock(CloseableIterator.class);
		expect(readerMock.next()).andThrow(new IOException("Test error"));
		readerMock.close();
		readerFactoryStub.predefinedReader = readerMock;
		control.replay();
		
		handler.subscribe(consumerMock1);
		
		control.verify();
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testUnsubscribe_RemoveConsumer() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		
		handler.unsubscribe(consumerMock1);
		
		assertHandlerSequenceIsStillActive(1);
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock2.consume(task.getUpdate());
		control.replay();
		handler.consume(task.getUpdate(), 1);
		control.verify();
	}
	
	@Test
	public void testUnsubscribe_FinishSequenceIfNoMoreConsumers() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		
		handler.unsubscribe(consumerMock1);
		handler.unsubscribe(consumerMock2);
		
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testStartNewSequence_IfHasConsumers() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		CloseableIteratorStub<L1Update> readerStub1 =
			(CloseableIteratorStub<L1Update>) readerFactoryStub.predefinedReader;
		List<L1Update> input = new ArrayList<>();
		input.add(new L1UpdateBuilder(symbol)
			.withTime("1998-08-13T00:00:00Z")
			.withTrade()
			.withPrice(42.12d)
			.withSize(1000L)
			.buildL1Update());
		CloseableIteratorStub<L1Update> readerStub2 = new CloseableIteratorStub<>(input);
		readerFactoryStub.predefinedReader = readerStub2;
		schedulerStub.clearScheduledTasks();
		
		handler.startNewSequence();
		
		// Initial reader is closed, sequence is obsolete
		assertTrue(readerStub1.isClosed());
		assertHandlerSequenceIsObsolete(1);
		assertHandlerSequenceIsStillActive(2);
		// New sequence task scheduled
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("1998-08-13T00:00:00Z"),
				new L1UpdateTask(input.get(0), 2, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
	}
	
	@Test
	public void testStartNewSequence_IfNoConsumers() throws Exception {
		@SuppressWarnings("unchecked")
		CloseableIterator<L1Update> readerMock = control.createMock(CloseableIterator.class);
		readerFactoryStub.predefinedReader = readerMock;
		control.replay();
		
		handler.startNewSequence();
		
		control.verify();
		assertHandlerSequenceIsStillActive(1); // actually was not open
	}
	
	@Test
	public void testConsume_SkipObsoleteTask() throws Exception {
		testUnsubscribe_FinishSequenceIfNoMoreConsumers();
	}
	
	@Test
	public void testConsume_ScheduleNextUpdate() throws Exception {
		List<L1Update> input = new ArrayList<>();
		input.add(new L1UpdateBuilder(symbol)
			.withTime("1917-10-01T00:00:00Z")
			.withTrade()
			.withPrice(80.01d)
			.withSize(100L)
			.buildL1Update());
		input.add(new L1UpdateBuilder(symbol)
			.withTime("1918-01-01T00:00:00Z")
			.withTrade()
			.withPrice(75.08d)
			.withSize(200L)
			.buildL1Update());
		CloseableIteratorStub<L1Update> readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		consumerMock1.consume(input.get(0));
		control.replay();
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock);
		handler.subscribe(consumerMock1);
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		schedulerStub.clearScheduledTasks();
		
		handler.consume(task.getUpdate(), 1);
		
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("1918-01-01T00:00:00Z"),
				new L1UpdateTask(input.get(1), 1, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertFalse(readerStub.isClosed());
		assertHandlerSequenceIsStillActive(1);
	}
	
	@Test
	public void testConsume_FinishSequenceIfNoMoreUpdates() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		CloseableIteratorStub<L1Update> readerStub =
				(CloseableIteratorStub<L1Update>) readerFactoryStub.predefinedReader;
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock1.consume(task.getUpdate());
		control.replay();
		
		handler.consume(task.getUpdate(), 1);
		
		control.verify();
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testConsume_FinishSequenceIfNoConsumers() throws Exception {
		List<L1Update> input = new ArrayList<>();
		input.add(new L1UpdateBuilder(symbol)
			.withTime("1917-10-01T00:00:00Z")
			.withTrade()
			.withPrice(80.01d)
			.withSize(100L)
			.buildL1Update());
		input.add(new L1UpdateBuilder(symbol)
			.withTime("1918-01-01T00:00:00Z")
			.withTrade()
			.withPrice(75.08d)
			.withSize(200L)
			.buildL1Update());
		CloseableIteratorStub<L1Update> readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		control.replay();
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock);
		handler.subscribe(consumerMock1);
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		schedulerStub.clearScheduledTasks();
		handler.unsubscribe(consumerMock1);
		
		handler.consume(task.getUpdate(), 1);
		
		List<SchedulerStubTask> expected = new ArrayList<>();
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
	}

	@Test
	public void testClose() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		CloseableIteratorStub<L1Update> readerStub =
				(CloseableIteratorStub<L1Update>) readerFactoryStub.predefinedReader;
		schedulerStub.clearScheduledTasks();
		control.replay();
		
		handler.close();
		
		control.verify();
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
		control.resetToStrict();
		@SuppressWarnings("unchecked")
		CloseableIterator<L1Update> readerMock = control.createMock(CloseableIterator.class);
		readerFactoryStub.predefinedReader = readerMock;
		control.replay();
		handler.startNewSequence(); // should not read  updates
		control.verify();
	}
	
	@Test
	public void testSetStartTime_AffectsSubscribe() throws Exception {
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock, consumersStub);
		handler.setStartTime(T("2017-08-06T19:30:00Z"));
		expect(readerFactoryMock.createReader(symbol, T("2017-08-06T19:30:00Z")))
			.andReturn(new CloseableIteratorStub<>());
		control.replay();

		handler.subscribe(consumerMock1);
		
		control.verify();
	}
	
	@Test
	public void testSetStartTime_AffectsStartNewSequence() throws Exception {
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock, consumersStub);
		handler.setStartTime(T("2017-08-06T19:30:00Z"));
		consumersStub.add(consumerMock1);

		expect(readerFactoryMock.createReader(symbol, T("2017-08-06T19:30:00Z")))
		.andReturn(new CloseableIteratorStub<>());
		control.replay();
	
		handler.startNewSequence();
		
		control.verify();
	}

}
