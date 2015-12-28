package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-20<br>
 * $Id: CandleTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleTest {
	private LocalDateTime from, to;
	private Interval interval1, interval2; 
	private Candle c1, c2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		from = LocalDateTime.of(2013, 10, 5, 19, 40, 0);
		to = LocalDateTime.of(2013, 10, 5, 19, 45, 0);
		interval1 = Interval.of(from.toInstant(ZoneOffset.UTC),
				to.toInstant(ZoneOffset.UTC));
		interval2 = Interval.of(LocalDateTime.of(2013, 10, 5, 19, 50, 0)
				.toInstant(ZoneOffset.UTC), Duration.of(5,  ChronoUnit.MINUTES));
		c2 = new Candle(interval2, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
	}
	
	@Test
	public void testGetStartTime() throws Exception {
		assertEquals(LocalDateTime.of(2013, 10, 5, 19, 50, 0, 0), c2.getStartTime());
	}
	
	@Test
	public void testGetEndTime() throws Exception {
		assertEquals(LocalDateTime.of(2013, 10, 5, 19, 55, 0, 0), c2.getEndTime());
	}
	
	@Test
	public void testConstruct6() throws Exception {
		c1 = new Candle(interval1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		assertEquals(interval1, c1.getInterval());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(125.15d, c1.getClose(),0.001d);
		assertEquals(10000L,  c1.getVolume());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		c1 = new Candle(c2);
		assertEquals(interval2, c1.getInterval());
		assertEquals(120.05d, c1.getOpen(), 0.001d);
		assertEquals(130.00d, c1.getHigh(), 0.001d);
		assertEquals( 90.55d, c1.getLow(),  0.001d);
		assertEquals(125.15d, c1.getClose(),0.001d);
		assertEquals(10000L,  c1.getVolume());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		c1 = new Candle(interval1, 120.5d, 1000L);
		assertEquals(interval1, c1.getInterval());
		assertEquals(120.5d, c1.getOpen(), 0.001d);
		assertEquals(120.5d, c1.getHigh(), 0.001d);
		assertEquals(120.5d, c1.getLow(),  0.001d);
		assertEquals(120.5d, c1.getClose(),0.001d);
		assertEquals(1000L,  c1.getVolume());
	}
	
	@Test
	public void testAddDeal2() throws Exception {
		c1 = new Candle(interval1, 120.05d, 130.00d, 90.55d, 125.15d, 10000L);
		c2 = c1.addDeal(132.25d, 1L);
		assertNotNull(c2);
		assertNotSame(c1, c2);
		assertEquals(interval1, c2.getInterval());
		assertEquals(120.05d, c2.getOpen(), 0.001d);
		assertEquals(132.25d, c2.getHigh(), 0.001d);
		assertEquals( 90.55d, c2.getLow(),  0.001d);
		assertEquals(132.25d, c2.getClose(),0.001d);
		assertEquals(10001L,  c2.getVolume());
	}
	
	@Test
	public void testAddCandle() throws Exception {
		c1 = new Candle(interval1, 120.05d, 130.00d, 80.55d, 125.15d, 10000L);
		interval2 = Interval.of(LocalDateTime.of(2013, 10, 5, 19, 44, 0, 0)
				.toInstant(ZoneOffset.UTC), Duration.of(1, ChronoUnit.MINUTES));
		c2 = new Candle(interval2, 140.05d, 150.00d, 90.55d, 128.00d,   100L);
		Candle c3 = c1.addCandle(c2);
		assertNotNull(c3);
		assertNotSame(c1, c3);
		assertNotSame(c2, c3);
		assertEquals(interval1, c3.getInterval());
		assertEquals(120.05d, c3.getOpen(), 0.001d);
		assertEquals(150.00d, c3.getHigh(), 0.001d);
		assertEquals( 80.55d, c3.getLow(),  0.001d);
		assertEquals(128.00d, c3.getClose(),0.001d);
		assertEquals(10100L,  c3.getVolume());
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAddCandle_ThrowsIfOutOfInterval() throws Exception {
		c1 = new Candle(interval1, 120.05d, 130.00d, 80.55d, 125.15d, 10000L);
		c2 = new Candle(interval2, 140.05d, 150.00d, 90.55d, 128.00d,   100L);
		c1.addCandle(c2);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Interval> vInt = new Variant<Interval>()
			.add(interval2)
			.add(interval1);
		Variant<Double> vOpen = new Variant<Double>(vInt)
			.add(120.05d)
			.add(300.01d);
		Variant<Double> vHigh = new Variant<Double>(vOpen)
			.add(130.00d)
			.add(320.00d);
		Variant<Double> vLow = new Variant<Double>(vHigh)
			.add(90.55d)
			.add(290.05d);
		Variant<Double> vClose = new Variant<Double>(vLow)
			.add(125.15d)
			.add(298.15d);
		Variant<Long> vVol = new Variant<Long>(vClose)
			.add(10000L)
			.add(500L);
		Variant<?> iterator = vVol;
		Candle x, found = null;
		int foundCnt = 0;
		do {
			x = new Candle(vInt.get(), vOpen.get(), vHigh.get(), vLow.get(),
					vClose.get(), vVol.get());
			if ( c2.equals(x) ) {
				foundCnt ++;
				found = x;
			}
			
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(interval2, found.getInterval());
		assertEquals(120.05d, found.getOpen(), 0.001d);
		assertEquals(130.00d, found.getHigh(), 0.001d);
		assertEquals( 90.55d, found.getLow(),  0.001d);
		assertEquals(125.15d, found.getClose(),0.001d);
		assertEquals(10000L, found.getVolume());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(c2.equals(c2));
		assertFalse(c2.equals(null));
		assertFalse(c2.equals(this));
	}
	
	@Test
	public void testGetBody() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertEquals(34.63d, c1.getBody(), 0.001d);
		assertEquals(1.17d, c2.getBody(), 0.001d);
	}
	
	@Test
	public void testGetBodyMiddle() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertEquals(102.735d, c1.getBodyMiddle(), 0.0001d);
		assertEquals(96.735d, c2.getBodyMiddle(), 0.0001d);
	}
	
	@Test
	public void testGetBodyMiddleOrCloseIfBullish() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertEquals(102.735d, c1.getBodyMiddleOrCloseIfBullish(), 0.0001d);
		assertEquals(97.32d, c2.getBodyMiddleOrCloseIfBullish(), 0.0001d);
	}
	
	@Test
	public void testGetBodyMiddleOrCloseIfBearish() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertEquals(85.42d, c1.getBodyMiddleOrCloseIfBearish(), 0.0001d);
		assertEquals(96.735d, c2.getBodyMiddleOrCloseIfBearish(), 0.0001d);
	}
	
	@Test
	public void testIsBullish() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertFalse(c1.isBullish());
		assertTrue(c2.isBullish());
	}
	
	@Test
	public void testIsBearish() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertTrue(c1.isBearish());
		assertFalse(c2.isBearish());
	}
	
	@Test
	public void testGetTopShadow() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertEquals(79.95d, c1.getTopShadow(), 0.001d);
		assertEquals(0.68d, c2.getTopShadow(), 0.001d);
	}
	
	@Test
	public void testGetBottomShadow() throws Exception {
		c1 = new Candle(interval1, 120.05d, 200d, 80d, 85.42d, 1L);
		c2 = new Candle(interval1, 96.15d, 98d, 90d, 97.32d, 1L);
		assertEquals(5.42d, c1.getBottomShadow(), 0.0001d);
		assertEquals(6.15d, c2.getBottomShadow(), 0.0001d);
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "Candle[T=" + interval2.getStart() + " PT5M, " +
				"O=120.05, H=130.0, L=90.55, C=125.15, V=10000]";
		assertEquals(expected, c2.toString());
	}
	
	@Test
	public void testAddTrade() throws Exception {
		Trade trade = new Trade(null);
		trade.setPrice(131.12d);
		trade.setQty(800L);
		trade.setTime(LocalDateTime.of(2013, 10, 5, 19, 54, 59, 999));
		Candle result = c2.addTrade(trade);
		assertNotNull(result);
		assertEquals(interval2, result.getInterval());
		assertEquals(120.05d, result.getOpen(), 0.001d);
		assertEquals(131.12d, result.getHigh(), 0.001d);
		assertEquals( 90.55d, result.getLow(), 0.001d);
		assertEquals(131.12d, result.getClose(), 0.001d);
		assertEquals(10800L, result.getVolume());
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAddTrade_ThrowsIfOutOfInterval() throws Exception {
		Trade trade = new Trade(null);
		trade.setTime(LocalDateTime.of(2013, 10, 5, 19, 55, 0, 0));
		c2.addTrade(trade);
	}
	
	@Test
	public void testAddTick() throws Exception {
		Tick tick1, tick2;
		// тик без опционального значения -> объем равен 0
		tick1 = new Tick(LocalDateTime.of(2013, 10, 5, 19, 50, 2), 131.12d);
		// тип раньше предыдущего, но в рамках интервала это игнорируется
		tick2 = new Tick(LocalDateTime.of(2013, 10, 5, 19, 50, 1), 90.12d, 2d);
		Candle result = c2.addTick(tick1).addTick(tick2);
		assertNotNull(result);
		assertEquals(interval2, result.getInterval());
		assertEquals(120.05d, result.getOpen(), 0.001d);
		assertEquals(131.12d, result.getHigh(), 0.001d);
		assertEquals( 90.12d, result.getLow(), 0.001d);
		assertEquals( 90.12d, result.getClose(), 0.001d);
		assertEquals(10002L, result.getVolume());
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAddTick_ThrowsIfOutOfInterval() throws Exception {
		c2.addTick(new Tick(LocalDateTime.of(2013, 10, 5, 19, 55, 0), 180d, 10d));
	}

}
