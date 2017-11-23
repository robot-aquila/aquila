package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
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
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
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
		c2 = new Candle(interval2,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("130.00"),
				CDecimalBD.of("90.55"),
				CDecimalBD.of("125.15"),
				CDecimalBD.of(10000L));
	}
	
	@Test
	public void testGetStartTime() throws Exception {
		assertEquals(T("2013-10-05T19:50:00Z"), c2.getStartTime());
	}
	
	@Test
	public void testGetEndTime() throws Exception {
		assertEquals(T("2013-10-05T19:55:00Z"), c2.getEndTime());
	}
	
	@Test
	public void testConstruct6() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("130.00"),
				CDecimalBD.of("90.55"),
				CDecimalBD.of("125.15"),
				CDecimalBD.of(10000L));
		assertEquals(interval1, c1.getInterval());
		assertEquals(CDecimalBD.of("120.05"), c1.getOpen());
		assertEquals(CDecimalBD.of("130.00"), c1.getHigh());
		assertEquals(CDecimalBD.of("90.55"),  c1.getLow());
		assertEquals(CDecimalBD.of("125.15"), c1.getClose());
		assertEquals(CDecimalBD.of(10000L),   c1.getVolume());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		c1 = new Candle(c2);
		assertEquals(interval2, c1.getInterval());
		assertEquals(CDecimalBD.of("120.05"), c1.getOpen());
		assertEquals(CDecimalBD.of("130.00"), c1.getHigh());
		assertEquals(CDecimalBD.of("90.55"),  c1.getLow());
		assertEquals(CDecimalBD.of("125.15"), c1.getClose());
		assertEquals(CDecimalBD.of(10000L),   c1.getVolume());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		c1 = new Candle(interval1, CDecimalBD.of("120.50"), CDecimalBD.of(1000L));
		assertEquals(interval1, c1.getInterval());
		assertEquals(CDecimalBD.of("120.50"), c1.getOpen());
		assertEquals(CDecimalBD.of("120.50"), c1.getHigh());
		assertEquals(CDecimalBD.of("120.50"), c1.getLow());
		assertEquals(CDecimalBD.of("120.50"), c1.getClose());
		assertEquals(CDecimalBD.of(1000L),  c1.getVolume());
	}
	
	@Test
	public void testAddDeal2() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("130.00"),
				CDecimalBD.of("90.55"),
				CDecimalBD.of("125.15"),
				CDecimalBD.of(10000L));
		c2 = c1.addDeal(CDecimalBD.of("132.25"), CDecimalBD.of(1L));
		assertNotNull(c2);
		assertNotSame(c1, c2);
		assertEquals(interval1, c2.getInterval());
		assertEquals(CDecimalBD.of("120.05"), c2.getOpen());
		assertEquals(CDecimalBD.of("132.25"), c2.getHigh());
		assertEquals(CDecimalBD.of("90.55"),  c2.getLow());
		assertEquals(CDecimalBD.of("132.25"), c2.getClose());
		assertEquals(CDecimalBD.of(10001L),   c2.getVolume());
	}
	
	@Test
	public void testAddCandle() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("130.00"),
				CDecimalBD.of("80.55"),
				CDecimalBD.of("125.15"),
				CDecimalBD.of(10000L));
		interval2 = Interval.of(LocalDateTime.of(2013, 10, 5, 19, 44, 0, 0)
				.toInstant(ZoneOffset.UTC), Duration.of(1, ChronoUnit.MINUTES));
		c2 = new Candle(interval2,
				CDecimalBD.of("140.05"),
				CDecimalBD.of("150.00"),
				CDecimalBD.of("90.55"),
				CDecimalBD.of("128.00"),
				CDecimalBD.of(100L));
		Candle c3 = c1.addCandle(c2);
		assertNotNull(c3);
		assertNotSame(c1, c3);
		assertNotSame(c2, c3);
		assertEquals(interval1, c3.getInterval());
		assertEquals(CDecimalBD.of("120.05"), c3.getOpen());
		assertEquals(CDecimalBD.of("150.00"), c3.getHigh());
		assertEquals(CDecimalBD.of("80.55"), c3.getLow());
		assertEquals(CDecimalBD.of("128.00"), c3.getClose());
		assertEquals(CDecimalBD.of(10100L),  c3.getVolume());
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAddCandle_ThrowsIfOutOfInterval() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("130.00"),
				CDecimalBD.of("80.55"),
				CDecimalBD.of("125.15"),
				CDecimalBD.of(10000L));
		c2 = new Candle(interval2,
				CDecimalBD.of("140.05"),
				CDecimalBD.of("150.00"),
				CDecimalBD.of("90.55"),
				CDecimalBD.of("128.00"),
				CDecimalBD.of(100L));
		c1.addCandle(c2);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Interval> vInt = new Variant<Interval>()
			.add(interval2)
			.add(interval1);
		Variant<CDecimal> vOpen = new Variant<CDecimal>(vInt)
				.add(CDecimalBD.of("120.05"))
				.add(CDecimalBD.of("300.01")),
			vHigh = new Variant<CDecimal>(vOpen)
				.add(CDecimalBD.of("130.00"))
				.add(CDecimalBD.of("320.00")),
			vLow = new Variant<CDecimal>(vHigh)
				.add(CDecimalBD.of("90.55"))
				.add(CDecimalBD.of("290.05")),
			vClose = new Variant<CDecimal>(vLow)
				.add(CDecimalBD.of("125.15"))
				.add(CDecimalBD.of("298.15")),
			vVol = new Variant<CDecimal>(vClose)
				.add(CDecimalBD.of(10000L))
				.add(CDecimalBD.of(500L));
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
		assertEquals(CDecimalBD.of("120.05"), found.getOpen());
		assertEquals(CDecimalBD.of("130.00"), found.getHigh());
		assertEquals(CDecimalBD.of("90.55"), found.getLow());
		assertEquals(CDecimalBD.of("125.15"), found.getClose());
		assertEquals(CDecimalBD.of(10000L), found.getVolume());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(c2.equals(c2));
		assertFalse(c2.equals(null));
		assertFalse(c2.equals(this));
	}
	
	@Test
	public void testGetBody() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertEquals(CDecimalBD.of("34.63"), c1.getBody());
		assertEquals(CDecimalBD.of("1.17"), c2.getBody());
	}
	
	@Test
	public void testGetBodyMiddle() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertEquals(CDecimalBD.of("102.74"), c1.getBodyMiddle());
		assertEquals(CDecimalBD.of("96.74"), c2.getBodyMiddle());
	}
	
	@Test
	public void testGetBodyMiddleOrCloseIfBullish() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertEquals(CDecimalBD.of("102.74"), c1.getBodyMiddleOrCloseIfBullish());
		assertEquals(CDecimalBD.of("97.32"), c2.getBodyMiddleOrCloseIfBullish());
	}
	
	@Test
	public void testGetBodyMiddleOrCloseIfBearish() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertEquals(CDecimalBD.of("85.42"), c1.getBodyMiddleOrCloseIfBearish());
		assertEquals(CDecimalBD.of("96.74"), c2.getBodyMiddleOrCloseIfBearish());
	}
	
	@Test
	public void testIsBullish() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertFalse(c1.isBullish());
		assertTrue(c2.isBullish());
	}
	
	@Test
	public void testIsBearish() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertTrue(c1.isBearish());
		assertFalse(c2.isBearish());
	}
	
	@Test
	public void testGetTopShadow() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertEquals(CDecimalBD.of("79.95"), c1.getTopShadow());
		assertEquals(CDecimalBD.of("0.68"), c2.getTopShadow());
	}
	
	@Test
	public void testGetBottomShadow() throws Exception {
		c1 = new Candle(interval1,
				CDecimalBD.of("120.05"),
				CDecimalBD.of("200.00"),
				CDecimalBD.of("80.00"),
				CDecimalBD.of("85.42"),
				CDecimalBD.of(1L));
		c2 = new Candle(interval1,
				CDecimalBD.of("96.15"),
				CDecimalBD.of("98.00"),
				CDecimalBD.of("90.00"),
				CDecimalBD.of("97.32"),
				CDecimalBD.of(1L));
		assertEquals(CDecimalBD.of("5.42"), c1.getBottomShadow());
		assertEquals(CDecimalBD.of("6.15"), c2.getBottomShadow());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "Candle[T=" + interval2.getStart() + " PT5M, " +
				"O=120.05, H=130.00, L=90.55, C=125.15, V=10000]";
		assertEquals(expected, c2.toString());
	}
	
	@Test
	public void testAddTick() throws Exception {
		Tick tick1, tick2;
		// TODO: Это все еще актуально?
		// тик без опционального значения -> объем равен 0
		tick1 = Tick.of(T("2013-10-05T19:50:02Z"), CDecimalBD.of("131.12"), CDecimalBD.of(0L));
		// тиr раньше предыдущего, но в рамках интервала это игнорируется
		tick2 = Tick.of(T("2013-10-05T19:50:01Z"), CDecimalBD.of("90.12"), CDecimalBD.of(2L));
		Candle result = c2.addTick(tick1).addTick(tick2);
		assertNotNull(result);
		assertEquals(interval2, result.getInterval());
		assertEquals(CDecimalBD.of("120.05"), result.getOpen());
		assertEquals(CDecimalBD.of("131.12"), result.getHigh());
		assertEquals(CDecimalBD.of("90.12"), result.getLow());
		assertEquals(CDecimalBD.of("90.12"), result.getClose());
		assertEquals(CDecimalBD.of(10002L), result.getVolume());
	}
	
	@Test (expected=OutOfIntervalException.class)
	public void testAddTick_ThrowsIfOutOfInterval() throws Exception {
		c2.addTick(Tick.of(T("2013-10-05T19:55:00Z"), CDecimalBD.of("180.00"), CDecimalBD.of(10L)));
	}

}
