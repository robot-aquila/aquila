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
	public void testAddAsk2_DD() throws Exception {
		Instant time = Instant.parse("2016-07-09T20:24:30Z");
		assertSame(builder, builder.withTime(time)
				.addAsk(CDecimalBD.of("120.01"), CDecimalBD.of(1000L))
				.addAsk(CDecimalBD.of("120.05"), CDecimalBD.of(2000L)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, "120.01", 1000L), MDTransactionType.ADD);
		expected.addRecord(Tick.ofAsk(time, "120.05", 2000L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddAsk2_SL() throws Exception {
		Instant time = Instant.parse("2016-07-09T20:24:30Z");
		assertSame(builder, builder.withTime(time)
				.addAsk("120.01", 1000L)
				.addAsk("120.05", 2000L));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, "120.01", 1000L), MDTransactionType.ADD);
		expected.addRecord(Tick.ofAsk(time, "120.05", 2000L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}

	@Test
	public void testAddBid2_DD() throws Exception {
		Instant time = Instant.parse("2016-07-09T20:26:45Z");
		assertSame(builder, builder.withTime(time)
				.addBid(CDecimalBD.of("150.38"), CDecimalBD.of(800L))
				.addBid(CDecimalBD.of("151.02"), CDecimalBD.of(400L)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofBid(time, "150.38", 800L), MDTransactionType.ADD);
		expected.addRecord(Tick.ofBid(time, "151.02", 400L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddBid2_SL() throws Exception {
		Instant time = Instant.parse("2016-07-09T20:26:45Z");
		assertSame(builder, builder.withTime(time)
				.addBid("150.38", 800L)
				.addBid("151.02", 400L));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofBid(time, "150.38", 800L), MDTransactionType.ADD);
		expected.addRecord(Tick.ofBid(time, "151.02", 400L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAdd1() throws Exception {
		Instant time = Instant.parse("2016-07-30T02:14:00Z");
		assertSame(builder, builder
				.add(Tick.ofAsk(time, "200.00", 100L))
				.add(Tick.ofBid(time, "250.00", 200L)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, "200.00", 100L), MDTransactionType.ADD);
		expected.addRecord(Tick.ofBid(time, "250.00", 200L), MDTransactionType.ADD);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplaceAsk() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.replaceAsk(CDecimalBD.of("200.00"), CDecimalBD.of(100L))
				.replaceAsk(CDecimalBD.of("250.00"), CDecimalBD.of(200L)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, "200.00", 100L), MDTransactionType.REPLACE);
		expected.addRecord(Tick.ofAsk(time, "250.00", 200L), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplaceBid() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.replaceBid(CDecimalBD.of("200.00"), CDecimalBD.of(100L))
				.replaceBid(CDecimalBD.of("250.00"), CDecimalBD.of(200L)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("200.00"), CDecimalBD.of(100L)), MDTransactionType.REPLACE);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("250.00"), CDecimalBD.of(200L)), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}

	@Test
	public void testDeleteAsk() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.deleteAsk(CDecimalBD.of("200.00"))
				.deleteAsk(CDecimalBD.of("250.00")));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, CDecimalBD.of("200.00"), CDecimalBD.ZERO), MDTransactionType.DELETE);
		expected.addRecord(Tick.ofAsk(time, CDecimalBD.of("250.00"), CDecimalBD.ZERO), MDTransactionType.DELETE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteBid() throws Exception {
		Instant time = Instant.parse("2016-07-30T03:28:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.deleteBid(CDecimalBD.of("200.00"))
				.deleteBid(CDecimalBD.of("250.00")));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("200.00"), CDecimalBD.ZERO), MDTransactionType.DELETE);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("250.00"), CDecimalBD.ZERO), MDTransactionType.DELETE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplaceOrDelete_DeleteWhenZeroSize() throws Exception {
		Instant time = Instant.parse("2016-08-12T11:55:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.replaceOrDelete(Tick.ofAsk(time, CDecimalBD.of("200.15"), CDecimalBD.ZERO)));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, CDecimalBD.of("200.15"), CDecimalBD.ZERO), MDTransactionType.DELETE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplaceOrDelete_ReplaceWhenNonZeroSize() throws Exception {
		Instant time = Instant.parse("2016-08-12T12:00:00Z");
		assertSame(builder, builder.withTime(time)
				.withType(MDUpdateType.UPDATE)
				.replaceOrDelete(Tick.ofBid(time, CDecimalBD.of("113.48"), CDecimalBD.of(200L))));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("113.48"), CDecimalBD.of(200L)), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTime_String() throws Exception {
		Instant time = Instant.parse("1978-02-15T14:48:20Z");
		assertSame(builder, builder.withTime("1978-02-15T14:48:20Z"));
		
		MDUpdate actual = builder.buildMDUpdate();

		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, time, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReplace1() throws Exception {
		Instant time = Instant.parse("2016-07-30T02:14:00Z");
		assertSame(builder, builder
				.replace(Tick.ofAsk(time, CDecimalBD.of("200.00"), CDecimalBD.of(100L)))
				.replace(Tick.ofBid(time, CDecimalBD.of("250.00"), CDecimalBD.of(200L))));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, CDecimalBD.of("200.00"), CDecimalBD.of(100L)), MDTransactionType.REPLACE);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("250.00"), CDecimalBD.of(200L)), MDTransactionType.REPLACE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDelete1() throws Exception {
		Instant time = Instant.parse("2016-07-30T02:14:00Z");
		assertSame(builder, builder
				.delete(Tick.ofAsk(time, CDecimalBD.of("200.00"), CDecimalBD.of(100L)))
				.delete(Tick.ofBid(time, CDecimalBD.of("250.00"), CDecimalBD.of(200L))));
		
		MDUpdate actual = builder.buildMDUpdate();
		
		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		expected.addRecord(Tick.ofAsk(time, CDecimalBD.of("200.00"), CDecimalBD.of(100L)), MDTransactionType.DELETE);
		expected.addRecord(Tick.ofBid(time, CDecimalBD.of("250.00"), CDecimalBD.of(200L)), MDTransactionType.DELETE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTypeRefresh() throws Exception {
		assertSame(builder, builder.withTime(Instant.EPOCH)
				.withTypeRefresh());
		
		MDUpdate actual = builder.buildMDUpdate();

		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}

	@Test
	public void testWithTypeRefreshAsk() throws Exception {
		assertSame(builder, builder.withTime(Instant.EPOCH)
				.withTypeRefreshAsk());
		
		MDUpdate actual = builder.buildMDUpdate();

		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH_ASK, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTypeRefreshBid() throws Exception {
		assertSame(builder, builder.withTime(Instant.EPOCH)
				.withTypeRefreshBid());
		
		MDUpdate actual = builder.buildMDUpdate();

		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.REFRESH_BID, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTypeUpdate() throws Exception {
		assertSame(builder, builder.withTime(Instant.EPOCH)
				.withTypeUpdate());
		
		MDUpdate actual = builder.buildMDUpdate();

		expectedHeader = new MDUpdateHeaderImpl(MDUpdateType.UPDATE, Instant.EPOCH, symbol1);
		expected = new MDUpdateImpl(expectedHeader);
		assertEquals(expected, actual);
	}

}
