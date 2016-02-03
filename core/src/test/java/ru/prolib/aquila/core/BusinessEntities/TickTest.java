package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.utils.Variant;

public class TickTest {
	private static LocalDateTime time1, time2;
	private static Instant time3;
	private Tick tick;
	
	@BeforeClass
	public static void setUpBeforClass() throws Exception {
		time1 = LocalDateTime.of(2013, 10, 6, 15, 44, 51, 123000000);
		time2 = LocalDateTime.of(2013, 6, 2, 11, 49, 24, 861000000);
		time3 = Instant.parse("2015-08-12T08:15:35.526Z");		
	}

	@Before
	public void setUp() throws Exception {
		tick = Tick.of(time1, 1828.14d, 1000);
	}
	
	@Test
	public void testOf3IDL() throws Exception {
		tick = Tick.of(time3, 1828.14d, 1000);
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(1828.14d, tick.getPrice(), 0.01d);
		assertEquals(1000.0d, tick.getSize(), 0.01d);
		assertEquals(0.0d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOf2TD() throws Exception {
		tick = Tick.of(time1, 1828.14d);
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time1.toInstant(ZoneOffset.UTC), tick.getTime());
		assertEquals(1828.14d, tick.getPrice(), 0.01d);
		assertEquals(0, tick.getSize());
		assertEquals(0, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOf2ID() throws Exception {
		tick = Tick.of(time3, 80.32d);
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(80.32d, tick.getPrice(), 0.01d);
		assertEquals(0, tick.getSize());
		assertEquals(0.0d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOf3TDL() throws Exception {
		tick = Tick.of(time1, 256.27d, 100);
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time1.toInstant(ZoneOffset.UTC), tick.getTime());
		assertEquals(256.27d, tick.getPrice(), 0.01d);
		assertEquals(100, tick.getSize());
		assertEquals(0.0d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOfTtIDL() throws Exception {
		tick = Tick.of(TickType.TRADE, time3, 65.17d, 150);
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(65.17d, tick.getPrice(), 0.01d);
		assertEquals(150, tick.getSize());
		assertEquals(0.0d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOfTtIDLD() throws Exception {
		tick = Tick.of(TickType.BID, time3, 713.45d, 820, 634.02d);
		assertSame(TickType.BID, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(713.45d, tick.getPrice(), 0.01d);
		assertEquals(820, tick.getSize());
		assertEquals(634.02d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOfTtLDLD() throws Exception {
		tick = Tick.of(TickType.TRADE, 1000, 824.15d, 420, 9921.82d);
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(1000, tick.getTimestamp());
		assertEquals(Instant.parse("1970-01-01T00:00:01Z"), tick.getTime());
		assertEquals(824.15d, tick.getPrice(), 0.01d);
		assertEquals(420, tick.getSize());
		assertEquals(9921.82d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testOfTlDL() throws Exception {
		tick = Tick.of(TickType.ASK, 814d, 1000);
		assertEquals(TickType.ASK, tick.getType());
		assertEquals(0, tick.getTimestamp());
		assertEquals(814d, tick.getPrice(), 0.01d);
		assertEquals(1000, tick.getSize());
		assertEquals(0.0d, tick.getValue(), 0.01d);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(tick.equals(tick));
		assertFalse(tick.equals(null));
		assertFalse(tick.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Tick expected = Tick.of(TickType.ASK, time3, 80.34d, 100, 803400.0d);
		Variant<TickType> vType = new Variant<TickType>()
				.add(TickType.ASK)
				.add(TickType.BID)
				.add(TickType.TRADE);
		Variant<Instant> vTime = new Variant<Instant>(vType)
				.add(time3)
				.add(time1.toInstant(ZoneOffset.UTC))
				.add(time2.toInstant(ZoneOffset.UTC));
		Variant<Double> vPrice = new Variant<Double>(vTime)
				.add(80.34d)
				.add(1828.14d)
				.add(5268.20d);
		Variant<Long> vSize = new Variant<Long>(vPrice)
				.add(100L)
				.add(1000L)
				.add(824L);
		Variant<Double> vValue = new Variant<Double>(vSize)
				.add(803400.0d)
				.add(215.4567d);
		Variant<?> iterator = vValue;
		int foundCnt = 0;
		Tick found = null, x = null;
		do {
			x = Tick.of(vType.get(), vTime.get(), vPrice.get(), vSize.get(), vValue.get());
			if ( expected.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(TickType.ASK, found.getType());
		assertEquals(time3, found.getTime());
		assertEquals(80.34d, found.getPrice(), 0.001d);
		assertEquals(100L, found.getSize(), 0.001d);
		assertEquals(803400.0d, found.getValue(), 0.001d);
	}
	
	@Test
	public void testToString() throws Exception {
		String expected[] = {
				"TRADE[2013-10-06T15:44:51.123Z 1828.14x1000]",
				"ASK[2015-08-12T08:15:35.526Z 34.15x100]",
				"BID[2015-08-12T08:15:35.526Z 34.15x100 425.95]",
				"TRADE[2015-08-12T08:15:35.526Z 34.15x100 425.95]"
		};
		Tick toTest[] = {
				tick,
				Tick.of(TickType.ASK,	time3, 34.15, 100),
				Tick.of(TickType.BID,	time3, 34.15, 100, 425.95),
				Tick.of(TickType.TRADE,	time3, 34.15, 100, 425.95)
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], toTest[i].toString());			
		}
	}

}
