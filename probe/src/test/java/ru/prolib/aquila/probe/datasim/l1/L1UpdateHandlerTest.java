package ru.prolib.aquila.probe.datasim.l1;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.probe.datasim.l1.L1UpdateHandler.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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

@SuppressWarnings("unchecked")
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
	private IBlockReader brMock;
	private CloseableIterator<L1Update> readerStub, readerMock, readerMock2;
	private L1UpdateHandler handler;
	private Set<L1UpdateConsumer> consumersStub;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		consumerMock1 = control.createMock(L1UpdateConsumer.class);
		consumerMock2 = control.createMock(L1UpdateConsumer.class);
		readerFactoryMock = control.createMock(L1UpdateReaderFactory.class);
		brMock = control.createMock(IBlockReader.class);
		readerMock = control.createMock(CloseableIterator.class);
		readerMock2 = control.createMock(CloseableIterator.class);
		schedulerStub = new SchedulerStub();
		schedulerStub.setFixedTime(T("2016-10-18T02:30:00Z"));
		readerFactoryStub = new UpdateReaderFactoryStub();
		consumersStub = new LinkedHashSet<>();
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryStub, consumersStub, brMock);
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
			.withPrice("120.19")
			.withSize(800L)
			.buildL1Update());
		return input;
	}
	
	private List<L1Update> getDefaultInputBlock() {
		List<L1Update> input = new ArrayList<>();
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2016-10-18T10:00:00Z");
		input.add(builder.withPrice("15.05").withSize(100).buildL1Update());
		input.add(builder.withPrice("15.07").withSize(150).buildL1Update());
		input.add(builder.withPrice("15.12").withSize(120).buildL1Update());
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
	public void testTimeBlockReader_JustOneItem() throws Exception {
		List<L1Update> fixture = getDefaultInput();
		TimeBlockReader service = new TimeBlockReader(new CloseableIteratorStub<>(fixture));
		
		List<L1Update> actual = service.readBlock();
		
		assertNotNull(actual);
		assertNull(service.getPendingUpdate());
		List<L1Update> expected = new ArrayList<>();
		expected.add(fixture.get(0));
	}
	
	@Test
	public void testTimeBlockReader_TwoConsecutiveBlocks() throws Exception {
		List<L1Update> fixture = new ArrayList<>();
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol)
				.withTime("2018-10-05T12:00:00Z")
				.withTrade();
		fixture.add(builder.withPrice("12.45").withSize(120L).buildL1Update());
		fixture.add(builder.withPrice("12.49").withSize(200L).buildL1Update());
		fixture.add(builder.withPrice("12.49").withSize(130L).buildL1Update());
		builder.withTime("2018-10-05T13:00:00Z");
		fixture.add(builder.withPrice("12.64").withSize(180L).buildL1Update());
		fixture.add(builder.withPrice("12.63").withSize(160L).buildL1Update());
		fixture.add(builder.withPrice("12.64").withSize(200L).buildL1Update());
		TimeBlockReader service = new TimeBlockReader(new CloseableIteratorStub<>(fixture));
		
		List<L1Update> actual = service.readBlock();
		
		assertNotNull(actual);
		assertEquals(fixture.get(3), service.getPendingUpdate());
		List<L1Update> expected = new ArrayList<>(fixture.subList(0, 3));
		assertEquals(expected, actual);

		actual = service.readBlock();
		assertNotNull(actual);
		assertNull(service.getPendingUpdate());
		expected = new ArrayList<>(fixture.subList(3, 6));
		assertEquals(expected, actual);
		
		actual = service.readBlock();
		assertNull(actual);
	}
	
	@Test
	public void testTimeBlockReader_LastBlockJustOneItem() throws Exception {
		List<L1Update> fixture = new ArrayList<>();
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol)
				.withTime("2018-10-05T12:00:00Z")
				.withTrade();
		fixture.add(builder.withPrice("12.45").withSize(120L).buildL1Update());
		fixture.add(builder.withPrice("12.49").withSize(200L).buildL1Update());
		fixture.add(builder.withPrice("12.49").withSize(130L).buildL1Update());
		builder.withTime("2018-10-05T13:00:00Z");
		fixture.add(builder.withPrice("12.64").withSize(180L).buildL1Update());
		TimeBlockReader service = new TimeBlockReader(new CloseableIteratorStub<>(fixture));
		
		List<L1Update> actual = service.readBlock();
		
		assertNotNull(actual);
		assertEquals(fixture.get(3), service.getPendingUpdate());
		List<L1Update> expected = new ArrayList<>(fixture.subList(0, 3));
		assertEquals(expected, actual);

		actual = service.readBlock();
		assertNotNull(actual);
		assertNull(service.getPendingUpdate());
		expected = new ArrayList<>(fixture.subList(3, 4));
		assertEquals(expected, actual);
		
		actual = service.readBlock();
		assertNull(actual);
	}

	@Test
	public void testTickByTickBlockReader_SetReader() throws Exception {
		List<L1Update> updates_cache = new ArrayList<>();
		IBlockReader service = new TickByTickBlockReader(brMock, updates_cache);
		brMock.setReader(readerMock2);
		control.replay();
		
		service.setReader(readerMock2);
		
		control.verify();
	}

	@Test
	public void testTickByTickBlockReader_ReadBlock_GetFromCache() throws Exception {
		List<L1Update> updates_cache = new ArrayList<>();
		IBlockReader service = new TickByTickBlockReader(brMock, updates_cache);
		L1Update update1, update2, update3;
		updates_cache.add(update1 = control.createMock(L1Update.class));
		updates_cache.add(update2 = control.createMock(L1Update.class));
		updates_cache.add(update3 = control.createMock(L1Update.class));
		control.replay();
		
		List<L1Update> actual = service.readBlock();
		
		control.verify();
		List<L1Update> expected = new ArrayList<>();
		expected.add(update1);
		assertEquals(expected, actual);
		
		expected.clear();
		expected.add(update2);
		expected.add(update3);
		assertEquals(expected, updates_cache);
	}
	
	@Test
	public void testTickByTickBlockReader_ReadBlock_LoadToCache() throws Exception {
		List<L1Update> base_reader_result = new ArrayList<>(), updates_cache = new ArrayList<>();
		IBlockReader service = new TickByTickBlockReader(brMock, updates_cache);
		L1Update update1, update2, update3;
		base_reader_result.add(update1 = control.createMock(L1Update.class));
		base_reader_result.add(update2 = control.createMock(L1Update.class));
		base_reader_result.add(update3 = control.createMock(L1Update.class));
		expect(brMock.readBlock()).andReturn(base_reader_result);
		control.replay();
		
		List<L1Update> actual = service.readBlock();
		
		control.verify();
		List<L1Update> expected = new ArrayList<>();
		expected.add(update1);
		assertEquals(expected, actual);
		
		expected.clear();
		expected.add(update2);
		expected.add(update3);
		assertEquals(expected, updates_cache);
	}
	
	@Test
	public void testTickByTickBlockReader_ReadBlock_NoMoreEntries_UnderlyinReturnEmptyList() throws Exception {
		List<L1Update> updates_cache = new ArrayList<>();
		IBlockReader service = new TickByTickBlockReader(brMock, updates_cache);
		expect(brMock.readBlock()).andReturn(new ArrayList<>());
		control.replay();
		
		List<L1Update> actual = service.readBlock();
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testTickByTickBlockReader_ReadBlock_NoMoreEntries_UnderlyingReturnNull() throws Exception {
		IBlockReader service = new TickByTickBlockReader(brMock, Arrays.asList());
		expect(brMock.readBlock()).andReturn(null); // This is valid behavior
		control.replay();
		
		List<L1Update> actual = service.readBlock();
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testCtor3() {
		L1UpdateHandler service = new L1UpdateHandler(symbol, schedulerStub, readerFactoryStub);
		
		IBlockReader actual = service.getBlockReader();
		
		assertNotNull(actual);
		assertEquals(TickByTickBlockReader.class, actual.getClass());
	}
	
	@Test
	public void testSubscribe_ScheduleUpdateAtFirstConsumer() throws Exception {
		List<L1Update> input = getDefaultInputBlock();
		readerStub = new CloseableIteratorStub<>(input);
		expect(readerFactoryMock.createReader(symbol, T("2016-10-18T02:30:00Z")))
			.andReturn(readerStub);
		brMock.setReader(readerStub);
		expect(brMock.readBlock()).andReturn(input);
		control.replay();
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock, consumersStub, brMock);
		
		handler.subscribe(consumerMock1);
		
		control.verify();
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("2016-10-18T10:00:00Z"),
				new L1UpdateTask(input, 1, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertHandlerSequenceIsStillActive(1);
	}
	
	@Test
	public void testSubscribe_SkipConsumerDuplicates() throws Exception {
		List<L1Update> input = getDefaultInput();
		readerFactoryStub.predefinedReader = readerMock;
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andReturn(input);
		control.replay();
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		handler.subscribe(consumerMock1);
		control.resetToStrict();
		
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock1.consume(task.getUpdates().get(0));
		consumerMock2.consume(task.getUpdates().get(0));
		expect(brMock.readBlock()).andReturn(null);
		readerMock.close();
		control.replay();
		handler.consume(task.getUpdates(), 1);
		control.verify();
	}
	
	@Test
	public void testSubscribe_FinishSequenceOnError() throws Exception {
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andThrow(new IOException("Test error"));
		readerMock.close();
		readerFactoryStub.predefinedReader = readerMock;
		control.replay();
		
		handler.subscribe(consumerMock1);
		
		control.verify();
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testUnsubscribe_RemoveConsumer() throws Exception {
		List<L1Update> input = getDefaultInput();
		readerFactoryStub.predefinedReader = readerMock;
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andReturn(input);
		control.replay();
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		control.resetToStrict();
		
		handler.unsubscribe(consumerMock1);
		
		assertHandlerSequenceIsStillActive(1);
		L1UpdateTask task = (L1UpdateTask) schedulerStub.getScheduledTasks().get(0).getRunnable();
		consumerMock2.consume(input.get(0));
		expect(brMock.readBlock()).andReturn(null);
		readerMock.close();
		control.replay();
		handler.consume(task.getUpdates(), 1);
		control.verify();
	}
	
	@Test
	public void testUnsubscribe_FinishSequenceIfNoMoreConsumers() throws Exception {
		List<L1Update> input = getDefaultInput();
		readerFactoryStub.predefinedReader = readerMock;
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andReturn(input);
		readerMock.close();
		control.replay();
		
		handler.subscribe(consumerMock1);
		handler.subscribe(consumerMock2);
		
		handler.unsubscribe(consumerMock1);
		handler.unsubscribe(consumerMock2);
		
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testStartNewSequence_IfHasConsumers() throws Exception {
		List<L1Update> input = getDefaultInput();
		readerFactoryStub.predefinedReader = readerMock;
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andReturn(input);
		control.replay();
		handler.subscribe(consumerMock1);
		control.resetToStrict();
		schedulerStub.clearScheduledTasks();
		input = new ArrayList<>();
		input.add(new L1UpdateBuilder(symbol)
			.withTime("1998-08-13T00:00:00Z")
			.withTrade()
			.withPrice("42.12")
			.withSize(1000L)
			.buildL1Update()); 
		readerFactoryStub.predefinedReader = readerMock2;
		readerMock.close();
		brMock.setReader(readerMock2);
		expect(brMock.readBlock()).andReturn(input);
		control.replay();
		
		handler.startNewSequence();
		
		// Initial reader is closed, sequence is obsolete
		assertHandlerSequenceIsObsolete(1);
		assertHandlerSequenceIsStillActive(2);
		// New sequence task scheduled
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("1998-08-13T00:00:00Z"),
				new L1UpdateTask(input, 2, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
	}
	
	@Test
	public void testStartNewSequence_IfNoConsumers() throws Exception {
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
		List<L1Update> updates1 = new ArrayList<>();
		updates1.add(control.createMock(L1Update.class));
		updates1.add(control.createMock(L1Update.class));
		List<L1Update> updates2 = new ArrayList<>();
		updates2.add(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("1918-01-01T00:00:00Z")
				.withPrice("13.67")
				.withSize(205)
				.buildL1Update());
		updates2.add(control.createMock(L1Update.class));
		updates2.add(control.createMock(L1Update.class));
		handler.setCurrentReader(readerMock);
		consumersStub.add(consumerMock1);
		consumerMock1.consume(updates1.get(0));
		consumerMock1.consume(updates1.get(1));
		expect(brMock.readBlock()).andReturn(updates2);
		control.replay();
		
		handler.consume(updates1, 1);
		
		List<SchedulerStubTask> expected = new ArrayList<>();
		expected.add(SchedulerStubTask.atTime(T("1918-01-01T00:00:00Z"),
				new L1UpdateTask(updates2, 1, handler)));
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertHandlerSequenceIsStillActive(1);
	}
	
	@Test
	public void testConsume_FinishSequenceIfNoMoreUpdates() throws Exception {
		List<L1Update> updates = getDefaultInput();
		handler.setCurrentReader(readerMock);
		consumersStub.add(consumerMock1);
		consumerMock1.consume(updates.get(0));
		expect(brMock.readBlock()).andReturn(null);
		readerMock.close();
		control.replay();
		
		handler.consume(updates, 1);
		
		control.verify();
		assertHandlerSequenceIsObsolete(1);
	}
	
	@Test
	public void testConsume_FinishSequenceIfNoConsumers() throws Exception {
		handler.setCurrentReader(readerMock);
		List<L1Update> updates = new ArrayList<>();
		updates.add(control.createMock(L1Update.class));
		updates.add(control.createMock(L1Update.class));
		readerMock.close();
		control.replay();
		
		handler.consume(updates, 1);
		
		List<SchedulerStubTask> expected = new ArrayList<>();
		assertEquals(expected, schedulerStub.getScheduledTasks());
		assertHandlerSequenceIsObsolete(1);
	}

	@Test
	public void testClose() throws Exception {
		useDefaultInput();
		CloseableIteratorStub<L1Update> readerStub =
				(CloseableIteratorStub<L1Update>) readerFactoryStub.predefinedReader;
		brMock.setReader(readerStub);
		expect(brMock.readBlock()).andReturn(getDefaultInput());
		control.replay();
		handler.subscribe(consumerMock1);
		schedulerStub.clearScheduledTasks();
		control.resetToStrict();
		control.replay();
		
		handler.close();
		
		control.verify();
		assertTrue(readerStub.isClosed());
		assertHandlerSequenceIsObsolete(1);
		control.resetToStrict();
		readerFactoryStub.predefinedReader = readerMock;
		control.replay();
		handler.startNewSequence(); // should not read  updates
		control.verify();
	}
	
	@Test
	public void testSetStartTime_AffectsSubscribe() throws Exception {
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock, consumersStub, brMock);
		handler.setStartTime(T("2017-08-06T19:30:00Z"));
		
		expect(readerFactoryMock.createReader(symbol, T("2017-08-06T19:30:00Z")))
			.andReturn(readerMock);
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andReturn(null);
		readerMock.close();
		control.replay();

		handler.subscribe(consumerMock1);
		
		control.verify();
	}
	
	@Test
	public void testSetStartTime_AffectsStartNewSequence() throws Exception {
		handler = new L1UpdateHandler(symbol, schedulerStub, readerFactoryMock, consumersStub, brMock);
		handler.setStartTime(T("2017-08-06T19:30:00Z"));
		consumersStub.add(consumerMock1);

		expect(readerFactoryMock.createReader(symbol, T("2017-08-06T19:30:00Z")))
			.andReturn(readerMock);
		brMock.setReader(readerMock);
		expect(brMock.readBlock()).andReturn(null);
		readerMock.close();
		control.replay();
	
		handler.startNewSequence();
		
		control.verify();
	}

}
