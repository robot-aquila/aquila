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
		asks.add(Tick.of(TickType.ASK, time, 200.45d, 100L));
		asks.add(Tick.of(TickType.ASK, time, 200.40d, 200L));
		asks.add(Tick.of(TickType.ASK, time, 200.35d, 500L));
		asks.add(Tick.of(TickType.ASK, time, 200.30d, 300L));
		List<Tick> bids = new ArrayList<Tick>();
		bids.add(Tick.of(TickType.BID, time, 201.10d, 200L));
		bids.add(Tick.of(TickType.BID, time, 201.20d, 400L));
		bids.add(Tick.of(TickType.BID, time, 201.30d, 600L));
		bids.add(Tick.of(TickType.BID, time, 201.40d, 800L));
		md = new MarketDepth(new Symbol("foo"), asks, bids, time, 2);
	}
	
	@Test
	public void testHasAskAtPriceLevel_WellRounded() {
		assertFalse(md.hasAskAtPriceLevel(200.4565d));
		assertTrue(md.hasAskAtPriceLevel(200.4545d));
		assertFalse(md.hasAskAtPriceLevel(200.4555d));
	}
	
	@Test
	public void testGetAskAtPriceLevel_WellRounded() {
		assertEquals(100L, md.getAskAtPriceLevel(200.4545d));
		assertEquals(0L, md.getAskAtPriceLevel(814.02d));
		assertEquals(200L, md.getAskAtPriceLevel(200.40d));
		assertEquals(200L, md.getAskAtPriceLevel(200.401234d));
		assertEquals(0L, md.getAskAtPriceLevel(200.40541d));
	}
	
	@Test
	public void testHasBidAtPriceLevel_WellRounded() {
		assertFalse(md.hasBidAtPriceLevel(201.11d));
		assertTrue(md.hasBidAtPriceLevel(201.10123d));
		assertTrue(md.hasBidAtPriceLevel(201.10456d));
		assertFalse(md.hasBidAtPriceLevel(201.10542d));
	}
	
	@Test
	public void testGetBidAtPriceLevel_WellRounded() {
		assertEquals(0L, md.getBidAtPriceLevel(201.11d));
		assertEquals(200L, md.getBidAtPriceLevel(201.10123d));
		assertEquals(200L, md.getBidAtPriceLevel(201.10456d));
		assertEquals(0L, md.getBidAtPriceLevel(201.10542d));
	}

}
