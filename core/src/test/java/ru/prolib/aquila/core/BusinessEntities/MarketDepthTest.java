package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MarketDepthTest {
	private MarketDepth md;

	@Before
	public void setUp() throws Exception {
		Instant time = Instant.now();
		List<Tick> asks = new ArrayList<Tick>();
		asks.add(Tick.ofAsk(time, CDecimalBD.of("200.45"), CDecimalBD.of(100L)));
		asks.add(Tick.ofAsk(time, CDecimalBD.of("200.40"), CDecimalBD.of(200L)));
		asks.add(Tick.ofAsk(time, CDecimalBD.of("200.35"), CDecimalBD.of(500L)));
		asks.add(Tick.ofAsk(time, CDecimalBD.of("200.30"), CDecimalBD.of(300L)));
		List<Tick> bids = new ArrayList<Tick>();
		bids.add(Tick.ofBid(time, CDecimalBD.of("201.10"), CDecimalBD.of(200L)));
		bids.add(Tick.ofBid(time, CDecimalBD.of("201.20"), CDecimalBD.of(400L)));
		bids.add(Tick.ofBid(time, CDecimalBD.of("201.30"), CDecimalBD.of(600L)));
		bids.add(Tick.ofBid(time, CDecimalBD.of("201.40"), CDecimalBD.of(800L)));
		md = new MarketDepth(new Symbol("foo"), asks, bids, time);
	}
	
	@Test
	public void testHasAskAtPriceLevel() {
		assertFalse(md.hasAskAtPriceLevel(CDecimalBD.of("200.29")));
		assertTrue(md.hasAskAtPriceLevel(CDecimalBD.of("200.45")));
		assertFalse(md.hasAskAtPriceLevel(CDecimalBD.of("200.46")));
	}
	
	@Test
	public void testGetAskAtPriceLevel() {
		assertEquals(CDecimalBD.of(100L), md.getAskAtPriceLevel(CDecimalBD.of("200.45")));
		assertEquals(CDecimalBD.of(0L),   md.getAskAtPriceLevel(CDecimalBD.of("814.02")));
		assertEquals(CDecimalBD.of(200L), md.getAskAtPriceLevel(CDecimalBD.of("200.40")));
		assertEquals(CDecimalBD.of(0L),   md.getAskAtPriceLevel(CDecimalBD.of("200.40241")));
	}
	
	@Test
	public void testHasBidAtPriceLevel() {
		assertFalse(md.hasBidAtPriceLevel(CDecimalBD.of("201.11")));
		assertFalse(md.hasBidAtPriceLevel(CDecimalBD.of("201.1012")));
		assertTrue(md.hasBidAtPriceLevel(CDecimalBD.of("201.10")));
		assertTrue(md.hasBidAtPriceLevel(CDecimalBD.of("201.20")));
	}
	
	@Test
	public void testGetBidAtPriceLevel() {
		assertEquals(CDecimalBD.of(0L)  , md.getBidAtPriceLevel(CDecimalBD.of("201.11")));
		assertEquals(CDecimalBD.of(200L), md.getBidAtPriceLevel(CDecimalBD.of("201.10")));
		assertEquals(CDecimalBD.of(0L),   md.getBidAtPriceLevel(CDecimalBD.of("201.11")));
	}

}
