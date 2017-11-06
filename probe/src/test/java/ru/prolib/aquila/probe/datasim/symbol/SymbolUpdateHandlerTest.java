package ru.prolib.aquila.probe.datasim.symbol;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStub;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStubTask;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateHandler;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateReaderFactory;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateTask;

public class SymbolUpdateHandlerTest {
	private static final Symbol symbol = new Symbol("SBER");
	
	static class UpdateReaderFactoryStub implements SymbolUpdateReaderFactory {
		private CloseableIterator<DeltaUpdate> predefinedReader;

		@Override
		public CloseableIterator<DeltaUpdate>
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
	private SymbolUpdateReaderFactory readerFactoryMock;
	private UpdateReaderFactoryStub readerFactoryStub;
	private DeltaUpdateConsumer consumerMock1, consumerMock2;
	private CloseableIterator<DeltaUpdate> readerStub;
	private SymbolUpdateHandler handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		consumerMock1 = control.createMock(DeltaUpdateConsumer.class);
		consumerMock2 = control.createMock(DeltaUpdateConsumer.class);
		readerFactoryMock = control.createMock(SymbolUpdateReaderFactory.class);
		schedulerStub = new SchedulerStub();
		schedulerStub.setFixedTime(T("2016-10-18T02:30:00Z"));
		readerFactoryStub = new UpdateReaderFactoryStub();
		handler = new SymbolUpdateHandler(symbol, schedulerStub, readerFactoryStub);
	}
	
	@After
	public void tearDown() throws Exception {
		if ( readerStub != null ) {
			readerStub.close();
			readerStub = null;
		}
	}
	
	private void useDefaultInput() {
		List<DeltaUpdate> input = new ArrayList<>();
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-18T02:30:00Z")
			.withToken(SecurityField.DISPLAY_NAME, "sbrf")
			.buildUpdate());
		readerFactoryStub.predefinedReader = new CloseableIteratorStub<>(input);
	}
	
	private void assertHandlerSequenceIsObsolete(int sequenceID) {
		assertNotEquals(sequenceID, handler.getCurrentSequenceID());
	}
	
	private void assertHandlerSequenceIsStillActive(int sequenceID) {
		assertEquals(sequenceID, handler.getCurrentSequenceID());
	}
	
	@Test
	public void testSubscribe_ScheduleUpdateAtFirstConsumer() throws Exception {
		List<DeltaUpdate> input = new ArrayList<>();
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-18T02:30:00Z")
			.withToken(SecurityField.DISPLAY_NAME, "sbrf")
			.buildUpdate());
		readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		control.replay();
		handler = new SymbolUpdateHandler(symbol, schedulerStub, readerFactoryMock);
		
		handler.subscribe(consumerMock1);
		
		control.verify();
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("2016-10-18T02:30:00Z"),
				new SymbolUpdateTask(symbol, input.get(0), 1, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertHandlerSequenceIsStillActive(1);
	}
	
	@Test
	public void testSubscribe_SkipConsumerDuplicates() throws Exception {
		useDefaultInput();
		
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		handler.subscribe(consumerMock1);
		
		SymbolUpdateTask task = (SymbolUpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock1.consume(task.getUpdate());
		consumerMock2.consume(task.getUpdate());
		control.replay();
		handler.consume(symbol, task.getUpdate(), 1);
		control.verify();
	}
	
	@Test
	public void testSubscribe_FinishSequenceOnError() throws Exception {
		@SuppressWarnings("unchecked")
		CloseableIterator<DeltaUpdate> readerMock = control.createMock(CloseableIterator.class);
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
		SymbolUpdateTask task = (SymbolUpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock2.consume(task.getUpdate());
		control.replay();
		handler.consume(symbol, task.getUpdate(), 1);
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
		CloseableIteratorStub<DeltaUpdate> readerStub1 =
				(CloseableIteratorStub<DeltaUpdate>) readerFactoryStub.predefinedReader;
		List<DeltaUpdate> input = new ArrayList<>();
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-18T04:00:00Z")
			.withToken(1028, "foobar")
			.buildUpdate());
		CloseableIteratorStub<DeltaUpdate> readerStub2 = new CloseableIteratorStub<>(input);
		readerFactoryStub.predefinedReader = readerStub2;
		schedulerStub.clearScheduledTasks();
		
		handler.startNewSequence();
		
		// Initial reader is closed, sequence is obsolete
		assertTrue(readerStub1.isClosed());
		assertHandlerSequenceIsObsolete(1);
		assertHandlerSequenceIsStillActive(2);
		// New sequence task scheduled
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("2016-10-18T04:00:00Z"),
				new SymbolUpdateTask(symbol, input.get(0), 2, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
	}
	
	@Test
	public void testStartNewSequence_IfNoConsumers() throws Exception {
		@SuppressWarnings("unchecked")
		CloseableIterator<DeltaUpdate> readerMock = control.createMock(CloseableIterator.class);
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
		List<DeltaUpdate> input = new ArrayList<>();
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-18T02:30:00Z")
			.withToken(1028, "sbrf")
			.buildUpdate());
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-10-18T03:00:00Z")
			.withToken(1029, 10)
			.buildUpdate());
		CloseableIteratorStub<DeltaUpdate> readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		consumerMock1.consume(input.get(0));
		control.replay();
		handler = new SymbolUpdateHandler(symbol, schedulerStub, readerFactoryMock);
		handler.subscribe(consumerMock1);
		SymbolUpdateTask task = (SymbolUpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		schedulerStub.clearScheduledTasks();
		
		handler.consume(symbol, task.getUpdate(), 1);
		
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("2016-10-18T03:00:00Z"),
				new SymbolUpdateTask(symbol, input.get(1), 1, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertFalse(readerStub.isClosed());
		assertHandlerSequenceIsStillActive(1);
	}
	
	@Test
	public void testConsume_FinishSequenceIfNoMoreUpdates() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		CloseableIteratorStub<DeltaUpdate> readerStub =
				(CloseableIteratorStub<DeltaUpdate>) readerFactoryStub.predefinedReader;
		SymbolUpdateTask task = (SymbolUpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock1.consume(task.getUpdate());
		control.replay();
		
		handler.consume(symbol, task.getUpdate(), 1);
		
		control.verify();
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
	}

	@Test
	public void testConsume_FinishSequenceIfNoConsumers() throws Exception {
		List<DeltaUpdate> input = new ArrayList<>();
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-18T02:30:00Z")
			.withToken(SecurityField.DISPLAY_NAME, "sbrf")
			.buildUpdate());
		input.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-10-18T03:00:00Z")
			.withToken(SecurityField.LOT_SIZE, 10)
			.buildUpdate());
		CloseableIteratorStub<DeltaUpdate> readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		control.replay();
		handler = new SymbolUpdateHandler(symbol, schedulerStub, readerFactoryMock);
		handler.subscribe(consumerMock1);
		SymbolUpdateTask task = (SymbolUpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		schedulerStub.clearScheduledTasks();
		handler.unsubscribe(consumerMock1);
		
		handler.consume(symbol, task.getUpdate(), 1);
		
		List<SchedulerStubTask> expected = new ArrayList<>();
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testClose() throws Exception {
		useDefaultInput();
		handler.subscribe(consumerMock1);
		CloseableIteratorStub<DeltaUpdate> readerStub =
				(CloseableIteratorStub<DeltaUpdate>) readerFactoryStub.predefinedReader;
		schedulerStub.clearScheduledTasks();
		control.replay();
		
		handler.close();
		
		control.verify();
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
		control.resetToStrict();
		@SuppressWarnings("unchecked")
		CloseableIterator<DeltaUpdate> readerMock = control.createMock(CloseableIterator.class);
		readerFactoryStub.predefinedReader = readerMock;
		control.replay();
		handler.startNewSequence(); // should not read  updates
		control.verify();
	}

}
