package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class MDUpdateBuilderTest {
	private static final Symbol symbol1 = new Symbol("AAPL");
	private static final Symbol symbol2 = new Symbol("MSFT");
	private MDUpdateBuilder builder;
	private MDUpdateHeader expectedHeader;
	private MDUpdateImpl expected;

	@Before
	public void setUp() throws Exception {
		expectedHeader = null;
		expected = null;
		builder = new MDUpdateBuilder(symbol1);
	}
	
	@Test
	public void testCtor0() {
		builder = new MDUpdateBuilder();
		builder.withSymbol(symbol2);
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol2);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCtor1() {
		builder = new MDUpdateBuilder(symbol1);
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithType() throws Exception {
		assertSame(builder, builder.withType(MDUpdateType.UPDATE));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithSymbol() throws Exception {
		assertSame(builder, builder.withSymbol(symbol2));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol2);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTime() throws Exception {
		assertSame(builder, builder.withTime(Instant.parse("2016-07-09T20:17:00Z")));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH,
				Instant.parse("2016-07-09T20:17:00Z"), symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddAsk2() throws Exception {
		Instant time = Instant.parse("2016-07-09T20:24:30Z");
		assertSame(builder, builder.withTime(time)
				.addAsk(120.01d, 1000L)
				.addAsk(120.05d, 2000L));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.ASK, time, 120.01d, 1000L), MDTransactionType.ADD);
		expected.addRecord(Tick.of(TickType.ASK, time, 120.05d, 2000L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}

	@Test
	public void testAddBid2() throws Exception {
		Instant time = Instant.parse("2016-07-09T20:26:45Z");
		assertSame(builder, builder.withTime(time)
				.addBid(150.38d, 800L)
				.addBid(151.02d, 400L));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.BID, time, 150.38d, 800L), MDTransactionType.ADD);
		expected.addRecord(Tick.of(TickType.BID, time, 151.02d, 400L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAdd1() throws Exception {
		Instant time = Instant.parse("2016-07-30T02:14:00Z");
		assertSame(builder, builder
				.add(Tick.of(TickType.ASK, time, 200.0d, 100L))
				.add(Tick.of(TickType.BID, time, 250.0d, 200L)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.ASK, time, 200.0d, 100L), MDTransactionType.ADD);
		expected.addRecord(Tick.of(TickType.BID, time, 250.0d, 200L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplaceAsk() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.replaceAsk(200.0d, 100L)
				.replaceAsk(250.0d, 200L));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.ASK, time, 200.0d, 100L), MDTransactionType.REPLACE);
		expected.addRecord(Tick.of(TickType.ASK, time, 250.0d, 200L), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplaceBid() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.replaceBid(200.0d, 100L)
				.replaceBid(250.0d, 200L));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.BID, time, 200.0d, 100L), MDTransactionType.REPLACE);
		expected.addRecord(Tick.of(TickType.BID, time, 250.0d, 200L), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}

	@Test
	public void testDeleteAsk() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.deleteAsk(200.0d)
				.deleteAsk(250.0d));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.ASK, time, 200.0d, 0), MDTransactionType.DELETE);
		expected.addRecord(Tick.of(TickType.ASK, time, 250.0d, 0), MDTransactionType.DELETE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteBid() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.deleteBid(200.0d)
				.deleteBid(250.0d));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.of(TickType.BID, time, 200.0d, 0), MDTransactionType.DELETE);
		expected.addRecord(Tick.of(TickType.BID, time, 250.0d, 0), MDTransactionType.DELETE);
		assertEquals(expected, actual);
	}

}
