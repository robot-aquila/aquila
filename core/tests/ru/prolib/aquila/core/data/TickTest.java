package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;

public class TickTest {
	private static Date time1, time2;
	private Tick tick;
	
	@BeforeClass
	public static void setUpBeforClass() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		time1 = df.parse("2013-03-07 15:53:00.123");
		time2 = df.parse("2013-03-07 16:00:00.001");
		
	}

	@Before
	public void setUp() throws Exception {
		tick = new Tick(time1, 1828.14d, 1000.0d);
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertSame(time1, tick.getTime());
		assertEquals(1828.14d, tick.getValue(), 0.01d);
		assertEquals(1000.0d, tick.getVolume(), 0.01d);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		tick = new Tick(time1, 1828.14d);
		assertSame(time1, tick.getTime());
		assertEquals(1828.14d, tick.getValue(), 0.01d);
		assertNull(tick.getVolume());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(tick.equals(tick));
		assertFalse(tick.equals(null));
		assertFalse(tick.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Date> vTime = new Variant<Date>()
			.add(time1)
			.add(time2);
		Variant<Double> vVal = new Variant<Double>(vTime)
			.add(1828.14d)
			.add(5268.20d);
		Variant<Double> vVol = new Variant<Double>(vVal)
			.add(1000.0d)
			.add(824d);
		Variant<?> iterator = vVol;
		int foundCnt = 0;
		Tick found = null, x = null;
		do {
			x = new Tick(vTime.get(), vVal.get(), vVol.get());
			if ( tick.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(time1, found.getTime());
		assertEquals(1828.14d, found.getValue(), 0.001d);
		assertEquals(1000.0d, found.getVolume(), 0.001d);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("Tick[val=1828.14, vol=1000.0 at 2013-03-07 15:53:00.123]",
				tick.toString());
	}

}
