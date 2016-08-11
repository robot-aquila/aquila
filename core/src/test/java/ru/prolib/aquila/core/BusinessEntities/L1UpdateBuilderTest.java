package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class L1UpdateBuilderTest {
	private static final Symbol symbol1 = new Symbol("AAPL"), symbol2 = new Symbol("MSFT");
	private L1UpdateBuilder builder;
	private L1Update expected, actual;

	@Before
	public void setUp() throws Exception {
		expected = actual = null;
		builder = new L1UpdateBuilder(symbol1);
	}
	
	@Test
	public void testCtor0() {
		actual = new L1UpdateBuilder().withSymbol(symbol1).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCtor1() {
		actual = new L1UpdateBuilder(symbol1).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTime() {
		actual = builder.withTime(Instant.parse("1997-12-01T00:00:00Z")).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE,
				Instant.parse("1997-12-01T00:00:00Z"), 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTime_Str() {
		actual = builder.withTime("1614-08-12T13:56:21Z").buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE,
				Instant.parse("1614-08-12T13:56:21Z"), 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildL1Update_ThrowsIfNullTime() {
		builder.withTime((Instant)null).buildL1Update();
	}

	@Test
	public void testWithSymbol() {
		actual = builder.withSymbol(symbol2).buildL1Update();
		
		expected = new L1UpdateImpl(symbol2, Tick.of(TickType.TRADE, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildL1Update_ThrowsIfNullSymbol() {
		builder.withSymbol(null).buildL1Update();
	}
	
	@Test
	public void testWithType() {
		actual = builder.withType(TickType.ASK).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.ASK, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildL1Update_ThrowsIfNullType() {
		builder.withType(null).buildL1Update();
	}
	
	@Test
	public void testWithAsk() {
		actual = builder.withAsk().buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.ASK, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithBid() {
		actual = builder.withBid().buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.BID, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTrade() {
		actual = builder.withTrade().buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE, Instant.EPOCH, 0.0d, 0L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithPrice() {
		actual = builder.withPrice(180.19d).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE, Instant.EPOCH, 180.19d, 0L));
		assertEquals(expected, actual);
	}

	@Test
	public void testWithSize() {
		actual = builder.withSize(2500L).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.TRADE, Instant.EPOCH, 0.0d, 2500L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testFromTick() {
		actual = builder.fromTick(Tick.of(TickType.BID,
				Instant.parse("2048-01-19T14:15:25Z"), 56.28d, 100L))
				.buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.of(TickType.BID,
				Instant.parse("2048-01-19T14:15:25Z"), 56.28d, 100L));
		assertEquals(expected, actual);
	}

}
