package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MDBuilderTest {
	private static Symbol symbol = new Symbol("MSFT");
	private MDBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new MDBuilder(symbol, 2);
		//builder.setPriceScale(2);
	}

	@Test
	public void testUpdate_Refresh() {
		Instant time = Instant.parse("2016-02-04T17:24:15Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withType(MDUpdateType.REFRESH)
			.withTime(time)
			.addBid(100.02d, 800)
			.addBid(102.45d, 100)
			.buildMDUpdate());
		builder.consume(new MDUpdateBuilder(symbol)
			.withType(MDUpdateType.REFRESH)
			.withTime(time)
			.addAsk(12.35d, 20)
			.addBid(12.28d, 30)
			.addAsk(12.33d, 10)
			.addBid(12.30d, 10)
			.addAsk(12.34d, 15)
			.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.33d, 10));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.34d, 15));
		expectedAsks.add(Tick.of(TickType.ASK, time, 12.35d, 20));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time, 12.30d, 10));
		expectedBids.add(Tick.of(TickType.BID, time, 12.28d, 30));
		MarketDepth expected = new MarketDepth(symbol, expectedAsks, expectedBids, time, 2);
		assertEquals(expected, builder.getMarketDepth());
	}

	@Test
	public void testUpdate_Replace() {
		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time1)
			.addAsk(102.45d, 100)
			.addAsk(102.40d, 150)
			.addAsk(102.30d, 120)
			.addAsk(102.00d, 220)
			.addAsk(101.00d, 450)
			.addBid(100.02d, 800)
			.addBid(100.01d, 100)
			.addBid( 99.98d, 500)
			.buildMDUpdate());
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time2)
			.withType(MDUpdateType.UPDATE)
			.replaceAsk(102.30d, 999)
			.replaceBid(99.98d, 199)
			.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time1, 101.00d, 450));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.00d, 220));
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.30d, 999));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.40d, 150));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.45d, 100));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time1, 100.02d, 800));
		expectedBids.add(Tick.of(TickType.BID, time1, 100.01d, 100));
		expectedBids.add(Tick.of(TickType.BID, time2,  99.98d, 199));
		MarketDepth expected = new MarketDepth(symbol, expectedAsks, expectedBids, time2, 2);
		assertEquals(expected, builder.getMarketDepth());
	}
	
	@Test
	public void testUpdate_Delete() {
		builder.setPriceScale(4);
		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time1)
			.addAsk(102.45d, 100)
			.addAsk(102.40d, 150)
			.addAsk(102.30d, 120)
			.addAsk(102.00d, 220)
			.addAsk(101.00d, 450)
			.addBid(100.02d, 800)
			.addBid(100.01d, 100)
			.addBid( 99.98d, 500)
			.buildMDUpdate());
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time2)
			.withType(MDUpdateType.UPDATE)
			.deleteAsk(102.30d)
			.deleteBid( 99.98d)
			.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time1, 101.00d, 450));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.00d, 220));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.40d, 150));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.45d, 100));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.of(TickType.BID, time1, 100.02d, 800));
		expectedBids.add(Tick.of(TickType.BID, time1, 100.01d, 100));
		MarketDepth expected = new MarketDepth(symbol, expectedAsks, expectedBids, time2, 4);
		assertEquals(expected, builder.getMarketDepth());
	}
	
	@Test
	public void testUpdate_PriceRouding() {
		builder.setPriceScale(2);

		Instant time1 = Instant.EPOCH;
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time1)
			.addAsk(102.30d, 120)
			.addAsk(102.40d, 150)
			.addAsk(102.45d, 100)
			.buildMDUpdate());

		Instant time2 = Instant.EPOCH.plusSeconds(1000);
		builder.consume(new MDUpdateBuilder(symbol)
				.withTime(time2)
				.withType(MDUpdateType.UPDATE)
				.replaceAsk(102.29651d, 500)
				.replaceAsk(102.40561d, 250)
				.replaceAsk(102.45021d, 200)
				.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.30d, 500));
		expectedAsks.add(Tick.of(TickType.ASK, time1, 102.40d, 150));
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.41d, 250));
		expectedAsks.add(Tick.of(TickType.ASK, time2, 102.45d, 200));
		assertEquals(expectedAsks, builder.getMarketDepth().getAsks());
	}
	
}
