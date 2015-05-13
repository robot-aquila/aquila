package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;

public class TickTest {
	private static DateTime time1, time2;
	private Tick tick;
	
	@BeforeClass
	public static void setUpBeforClass() throws Exception {
		time1 = new DateTime(2013, 10, 6, 15, 44, 51, 123);
		time2 = new DateTime(2013, 6, 2, 11, 49, 24, 861);
		
	}

	@Before
	public void setUp() throws Exception {
		tick = new Tick(time1, 1828.14d, 1000.0d);
	}
	
	@Test
	public void testConstruct3TDD() throws Exception {
		assertSame(time1, tick.getTime());
		assertEquals(1828.14d, tick.getValue(), 0.01d);
		assertEquals(1000.0d, tick.getOptionalValue(), 0.01d);
	}
	
	@Test
	public void testConstruct3TDL() throws Exception {
		tick = new Tick(time1, 256.27d, 100);
		assertEquals(time1, tick.getTime());
		assertEquals(256.27d, tick.getValue(), 0.01d);
		assertEquals(100, tick.getOptionalValueAsLong());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		tick = new Tick(time1, 1828.14d);
		assertSame(time1, tick.getTime());
		assertEquals(1828.14d, tick.getValue(), 0.01d);
		assertNull(tick.getOptionalValue());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(tick.equals(tick));
		assertFalse(tick.equals(null));
		assertFalse(tick.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<DateTime> vTime = new Variant<DateTime>()
			.add(time1)
			.add(time2);
		Variant<Double> vVal = new Variant<Double>(vTime)
			.add(1828.14d)
			.add(5268.20d);
		Variant<Double> vOpt = new Variant<Double>(vVal)
			.add(1000.0d)
			.add(824d);
		Variant<?> iterator = vOpt;
		int foundCnt = 0;
		Tick found = null, x = null;
		do {
			x = new Tick(vTime.get(), vVal.get(), vOpt.get());
			if ( tick.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(time1, found.getTime());
		assertEquals(1828.14d, found.getValue(), 0.001d);
		assertEquals(1000.0d, found.getOptionalValue(), 0.001d);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals(
			"Tick[val=1828.14, opt=1000.0 at 2013-10-06T15:44:51.123+04:00]",
			tick.toString());
	}
	
	@Test
	public void testGetOptionalValueAsLong() throws Exception {
		assertEquals(15L, new Tick(time1, 420d, 15d).getOptionalValueAsLong());
		assertEquals(0L, new Tick(time1, 420d).getOptionalValueAsLong());
	}

}
