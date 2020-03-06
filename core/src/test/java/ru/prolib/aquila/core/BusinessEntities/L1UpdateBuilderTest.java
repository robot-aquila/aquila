package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class L1UpdateBuilderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
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
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCtor1() {
		actual = new L1UpdateBuilder(symbol1).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTime() {
		actual = builder.withTime(Instant.parse("1997-12-01T00:00:00Z")).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(T("1997-12-01T00:00:00Z"),
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTime_Str() {
		actual = builder.withTime("1614-08-12T13:56:21Z").buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(T("1614-08-12T13:56:21Z"),
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildL1Update_ThrowsIfNullTime() {
		builder.withTime((Instant)null).buildL1Update();
	}

	@Test
	public void testWithSymbol() {
		actual = builder.withSymbol(symbol2).buildL1Update();
		
		expected = new L1UpdateImpl(symbol2, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildL1Update_ThrowsIfNullSymbol() {
		builder.withSymbol(null).buildL1Update();
	}
	
	@Test
	public void testWithType() {
		actual = builder.withType(TickType.ASK).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofAsk(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuildL1Update_ThrowsIfNullType() {
		builder.withType(null).buildL1Update();
	}
	
	@Test
	public void testWithAsk() {
		actual = builder.withAsk().buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofAsk(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithBid() {
		actual = builder.withBid().buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofBid(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithTrade() {
		actual = builder.withTrade().buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithPrice() {
		actual = builder.withPrice(CDecimalBD.of("180.19")).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.of("180.19"),
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithPrice_Str() {
		assertSame(builder, builder.withPrice("12.345"));
		actual = builder.buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.of("12.345"),
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithPrice_Long() {
		assertSame(builder, builder.withPrice(1200));
		actual = builder.buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.of(1200L),
				CDecimalBD.ZERO));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithSize() {
		actual = builder.withSize(CDecimalBD.of(2500L)).buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.of(2500L)));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithSize_Str() {
		assertSame(builder, builder.withSize("520"));
		actual = builder.buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.of(520L)));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithSize_Long() {
		assertSame(builder, builder.withSize(520L));
		actual = builder.buildL1Update();
				
		expected = new L1UpdateImpl(symbol1, Tick.ofTrade(Instant.EPOCH,
				CDecimalBD.ZERO,
				CDecimalBD.of(520L)));
		assertEquals(expected, actual);
	}

	@Test
	public void testFromTick() {
		actual = builder.fromTick(new Tick(TickType.BID, T("2048-01-19T14:15:25Z"), of("56.28"), of(100L), ZERO, "hello"))
			.buildL1Update();
		
		expected = new L1UpdateImpl(symbol1,
				new Tick(TickType.BID, T("2048-01-19T14:15:25Z"), of("56.28"), of(100L), ZERO, "hello"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithComment() {
		assertSame(builder, builder.withComment("zimbabwe"));
		actual = builder.buildL1Update();
		
		expected = new L1UpdateImpl(symbol1, new Tick(TickType.TRADE, Instant.EPOCH, ZERO, ZERO, ZERO, "zimbabwe"));
		assertEquals(expected, actual);
	}

}
