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
		builder = new MDBuilder(symbol);
	}

	@Test
	public void testUpdate_Refresh() {
		Instant time = Instant.parse("2016-02-04T17:24:15Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withType(MDUpdateType.REFRESH)
			.withTime(time)
			.addBid(CDecimalBD.of("100.02"), CDecimalBD.of(800L))
			.addBid(CDecimalBD.of("102.45"), CDecimalBD.of(100L))
			.buildMDUpdate());
		builder.consume(new MDUpdateBuilder(symbol)
			.withType(MDUpdateType.REFRESH)
			.withTime(time)
			.addAsk(CDecimalBD.of("12.35"), CDecimalBD.of(20L))
			.addBid(CDecimalBD.of("12.28"), CDecimalBD.of(30L))
			.addAsk(CDecimalBD.of("12.33"), CDecimalBD.of(10L))
			.addBid(CDecimalBD.of("12.30"), CDecimalBD.of(10L))
			.addAsk(CDecimalBD.of("12.34"), CDecimalBD.of(15L))
			.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.33"), CDecimalBD.of(10L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.34"), CDecimalBD.of(15L)));
		expectedAsks.add(Tick.ofAsk(time, CDecimalBD.of("12.35"), CDecimalBD.of(20L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.30"), CDecimalBD.of(10L)));
		expectedBids.add(Tick.ofBid(time, CDecimalBD.of("12.28"), CDecimalBD.of(30L)));
		MarketDepth expected = new MarketDepth(symbol, expectedAsks, expectedBids, time);
		assertEquals(expected, builder.getMarketDepth());
	}

	@Test
	public void testUpdate_Replace() {
		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time1)
			.addAsk(CDecimalBD.of("102.45"), CDecimalBD.of(100L))
			.addAsk(CDecimalBD.of("102.40"), CDecimalBD.of(150L))
			.addAsk(CDecimalBD.of("102.30"), CDecimalBD.of(120L))
			.addAsk(CDecimalBD.of("102.00"), CDecimalBD.of(220L))
			.addAsk(CDecimalBD.of("101.00"), CDecimalBD.of(450L))
			.addBid(CDecimalBD.of("100.02"), CDecimalBD.of(800L))
			.addBid(CDecimalBD.of("100.01"), CDecimalBD.of(100L))
			.addBid(CDecimalBD.of( "99.98"), CDecimalBD.of(500L))
			.buildMDUpdate());
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time2)
			.withType(MDUpdateType.UPDATE)
			.replaceAsk(CDecimalBD.of("102.30"), CDecimalBD.of(999L))
			.replaceBid(CDecimalBD.of( "99.98"), CDecimalBD.of(199L))
			.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("101.00"), CDecimalBD.of(450L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.00"), CDecimalBD.of(220L)));
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(999L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(100L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.02"), CDecimalBD.of(800L)));
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.01"), CDecimalBD.of(100L)));
		expectedBids.add(Tick.ofBid(time2, CDecimalBD.of( "99.98"), CDecimalBD.of(199L)));
		MarketDepth expected = new MarketDepth(symbol, expectedAsks, expectedBids, time2);
		assertEquals(expected, builder.getMarketDepth());
	}
	
	@Test
	public void testUpdate_Delete() {
		Instant time1 = Instant.parse("2016-02-04T18:23:00Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time1)
			.addAsk(CDecimalBD.of("102.45"), CDecimalBD.of(100L))
			.addAsk(CDecimalBD.of("102.40"), CDecimalBD.of(150L))
			.addAsk(CDecimalBD.of("102.30"), CDecimalBD.of(120L))
			.addAsk(CDecimalBD.of("102.00"), CDecimalBD.of(220L))
			.addAsk(CDecimalBD.of("101.00"), CDecimalBD.of(450L))
			.addBid(CDecimalBD.of("100.02"), CDecimalBD.of(800L))
			.addBid(CDecimalBD.of("100.01"), CDecimalBD.of(100L))
			.addBid(CDecimalBD.of( "99.98"), CDecimalBD.of(500L))
			.buildMDUpdate());
		Instant time2 = Instant.parse("2016-02-04T19:23:48Z");
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time2)
			.withType(MDUpdateType.UPDATE)
			.deleteAsk(CDecimalBD.of("102.30"))
			.deleteBid(CDecimalBD.of( "99.98"))
			.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("101.00"), CDecimalBD.of(450L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.00"), CDecimalBD.of(220L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.45"), CDecimalBD.of(100L)));
		List<Tick> expectedBids = new ArrayList<Tick>();
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.02"), CDecimalBD.of(800L)));
		expectedBids.add(Tick.ofBid(time1, CDecimalBD.of("100.01"), CDecimalBD.of(100L)));
		MarketDepth expected = new MarketDepth(symbol, expectedAsks, expectedBids, time2);
		assertEquals(expected, builder.getMarketDepth());
	}
	
	@Test
	public void testUpdate() {
		Instant time1 = Instant.EPOCH;
		builder.consume(new MDUpdateBuilder(symbol)
			.withTime(time1)
			.addAsk(CDecimalBD.of("102.30"), CDecimalBD.of(120L))
			.addAsk(CDecimalBD.of("102.40"), CDecimalBD.of(150L))
			.addAsk(CDecimalBD.of("102.45"), CDecimalBD.of(100L))
			.buildMDUpdate());

		Instant time2 = Instant.EPOCH.plusSeconds(1000);
		builder.consume(new MDUpdateBuilder(symbol)
				.withTime(time2)
				.withType(MDUpdateType.UPDATE)
				.replaceAsk(CDecimalBD.of("102.30"), CDecimalBD.of(500L))
				.replaceAsk(CDecimalBD.of("102.41"), CDecimalBD.of(250L))
				.replaceAsk(CDecimalBD.of("102.45"), CDecimalBD.of(200L))
				.buildMDUpdate());
		
		List<Tick> expectedAsks = new ArrayList<Tick>();
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.30"), CDecimalBD.of(500L)));
		expectedAsks.add(Tick.ofAsk(time1, CDecimalBD.of("102.40"), CDecimalBD.of(150L)));
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.41"), CDecimalBD.of(250L)));
		expectedAsks.add(Tick.ofAsk(time2, CDecimalBD.of("102.45"), CDecimalBD.of(200L)));
		assertEquals(expectedAsks, builder.getMarketDepth().getAsks());
	}
	
}
