package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.utils.Variant;

public class TickTest {
	private static Instant time1, time2, time3;
	private Tick tick;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforClass() throws Exception {
		time1 = T("2013-10-06T15:44:51.123Z");
		time2 = T("2013-06-02T11:49:24.861Z");
		time3 = T("2015-08-12T08:15:35.526Z");		
	}

	@Before
	public void setUp() throws Exception {
		tick = Tick.of(time1, CDecimalBD.of("1828.14"), CDecimalBD.of(1000L));
	}
	
	@Test
	public void testNullValues() {
		Tick dummy = Tick.NULL_ASK;
		assertNotNull(dummy);
		assertEquals(TickType.ASK, dummy.getType());
		assertEquals(Instant.EPOCH, dummy.getTime());
		assertEquals(CDecimalBD.ZERO, dummy.getPrice());
		assertEquals(CDecimalBD.ZERO, dummy.getSize());
		assertEquals(CDecimalBD.ZERO, dummy.getValue());
		assertNull(dummy.getComment());
		
		dummy = Tick.NULL_BID;
		assertNotNull(dummy);
		assertEquals(TickType.BID, dummy.getType());
		assertEquals(Instant.EPOCH, dummy.getTime());
		assertEquals(CDecimalBD.ZERO, dummy.getPrice());
		assertEquals(CDecimalBD.ZERO, dummy.getSize());
		assertEquals(CDecimalBD.ZERO, dummy.getValue());
		assertNull(dummy.getComment());
	}
	
	@Test
	public void testOf3TPS() throws Exception {
		tick = Tick.of(time3, CDecimalBD.of("1828.14"), CDecimalBD.of(1000L));
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(CDecimalBD.of("1828.14"), tick.getPrice());
		assertEquals(CDecimalBD.of(1000L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOf4_TISL() throws Exception {
		tick = Tick.of(TickType.ASK, time1, "26.92", 1205L);
		assertEquals(TickType.ASK, tick.getType());
		assertEquals(time1, tick.getTime());
		assertEquals(CDecimalBD.of("26.92"), tick.getPrice());
		assertEquals(CDecimalBD.of(1205L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOf2TP() throws Exception {
		tick = Tick.of(time1, CDecimalBD.of("1828.14"));
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time1, tick.getTime());
		assertEquals(CDecimalBD.of("1828.14"), tick.getPrice());
		assertEquals(CDecimalBD.ZERO, tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfTtTPS() throws Exception {
		tick = Tick.of(TickType.TRADE, time3, CDecimalBD.of("65.17"), CDecimalBD.of(150L));
		assertSame(TickType.TRADE, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(CDecimalBD.of("65.17"), tick.getPrice());
		assertEquals(CDecimalBD.of(150L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfTtTPSV() throws Exception {
		tick = Tick.of(TickType.BID, time3, CDecimalBD.of("713.45"),
				CDecimalBD.of(820L), CDecimalBD.ofRUB2("634.02"));
		assertSame(TickType.BID, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(CDecimalBD.of("713.45"), tick.getPrice());
		assertEquals(CDecimalBD.of(820L), tick.getSize());
		assertEquals(CDecimalBD.ofRUB2("634.02"), tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfTtPS() throws Exception {
		tick = Tick.of(TickType.ASK, CDecimalBD.of("814.00"), CDecimalBD.of(1000L));
		assertEquals(TickType.ASK, tick.getType());
		assertEquals(Instant.EPOCH, tick.getTime());
		assertEquals(CDecimalBD.of("814.00"), tick.getPrice());
		assertEquals(CDecimalBD.of(1000L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfAskTPS() throws Exception {
		tick = Tick.ofAsk(time3, CDecimalBD.of("18.34"), CDecimalBD.of(450L));
		assertEquals(TickType.ASK, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(CDecimalBD.of("18.34"), tick.getPrice());
		assertEquals(CDecimalBD.of(450L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfAskPS() throws Exception {
		tick = Tick.ofAsk(CDecimalBD.of("18.29"), CDecimalBD.of(580L));
		assertEquals(TickType.ASK, tick.getType());
		assertEquals(Instant.EPOCH, tick.getTime());
		assertEquals(CDecimalBD.of("18.29"), tick.getPrice());
		assertEquals(CDecimalBD.of(580L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfAsk3_ISL() throws Exception {
		tick = Tick.ofAsk(time2, "26.954", 280L);
		assertEquals(TickType.ASK, tick.getType());
		assertEquals(time2, tick.getTime());
		assertEquals(CDecimalBD.of("26.954"), tick.getPrice());
		assertEquals(CDecimalBD.of(280L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}

	@Test
	public void testOfBidTPS() throws Exception {
		tick = Tick.ofBid(time3, CDecimalBD.of("13.50"), CDecimalBD.of(1450L));
		assertEquals(TickType.BID, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(CDecimalBD.of("13.50"), tick.getPrice());
		assertEquals(CDecimalBD.of(1450L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfBidPS() throws Exception {
		tick = Tick.ofBid(CDecimalBD.of("115.02"), CDecimalBD.of(120L));
		assertEquals(TickType.BID, tick.getType());
		assertEquals(Instant.EPOCH, tick.getTime());
		assertEquals(CDecimalBD.of("115.02"), tick.getPrice());
		assertEquals(CDecimalBD.of(120L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfBid3_ISL() throws Exception {
		tick = Tick.ofBid(time3, "95.19", 400L);
		assertEquals(TickType.BID, tick.getType());
		assertEquals(CDecimalBD.of("95.19"), tick.getPrice());
		assertEquals(CDecimalBD.of(400L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}
	
	@Test
	public void testOfTradeTPS() throws Exception {
		tick = Tick.ofTrade(time3, CDecimalBD.of("2.12"), CDecimalBD.of(40L));
		assertEquals(TickType.TRADE, tick.getType());
		assertEquals(time3, tick.getTime());
		assertEquals(CDecimalBD.of("2.12"), tick.getPrice());
		assertEquals(CDecimalBD.of(40L), tick.getSize());
		assertEquals(CDecimalBD.ZERO, tick.getValue());
		assertNull(tick.getComment());
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(tick.equals(tick));
		assertFalse(tick.equals(null));
		assertFalse(tick.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Tick expected = new Tick(TickType.ASK, time3, CDecimalBD.of("80.34"),
				CDecimalBD.of(100L),
				CDecimalBD.ofUSD2("803400.00"),
				"Hello, Dolly!");
		Variant<TickType> vType = new Variant<TickType>()
				.add(TickType.ASK)
				.add(TickType.BID)
				.add(TickType.TRADE);
		Variant<Instant> vTime = new Variant<Instant>(vType)
				.add(time3)
				.add(time1)
				.add(time2);
		Variant<CDecimal> vPrice = new Variant<CDecimal>(vTime)
				.add(CDecimalBD.of("80.34"))
				.add(CDecimalBD.of("1828.14"))
				.add(CDecimalBD.of("5268.20"));
		Variant<CDecimal> vSize = new Variant<CDecimal>(vPrice)
				.add(CDecimalBD.of(100L))
				.add(CDecimalBD.of(1000L))
				.add(CDecimalBD.of(824L));
		Variant<CDecimal> vValue = new Variant<CDecimal>(vSize)
				.add(CDecimalBD.ofUSD2("803400.00"))
				.add(CDecimalBD.ofRUB5("215.4567"));
		Variant<String> vCom = new Variant<>(vValue, "Hello, Dolly!", "foobar");
		Variant<?> iterator = vCom;
		int foundCnt = 0;
		Tick found = null, x = null;
		do {
			x = new Tick(vType.get(), vTime.get(), vPrice.get(), vSize.get(), vValue.get(), vCom.get());
			if ( expected.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(TickType.ASK, found.getType());
		assertEquals(time3, found.getTime());
		assertEquals(CDecimalBD.of("80.34"), found.getPrice());
		assertEquals(CDecimalBD.of(100L), found.getSize());
		assertEquals(CDecimalBD.ofUSD2("803400.00"), found.getValue());
		assertEquals("Hello, Dolly!", found.getComment());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected[] = {
				"TRADE[2013-10-06T15:44:51.123Z 1828.14x1000 0]",
				"ASK[2015-08-12T08:15:35.526Z 34.15x100 0]",
				"BID[2015-08-12T08:15:35.526Z 34.15x100 425.95 RUB]",
				"TRADE[2015-08-12T08:15:35.526Z 34.15x100 425.95 USD]",
				"TRADE[2020-03-05T16:07:45.023Z 10.35x200 600.02 RUB note=Test]"
		};
		Tick toTest[] = {
				tick,
				Tick.of(TickType.ASK,	time3, CDecimalBD.of("34.15"), CDecimalBD.of(100L)),
				Tick.of(TickType.BID,	time3, CDecimalBD.of("34.15"),
						CDecimalBD.of(100L),
						CDecimalBD.ofRUB2("425.95")),
				Tick.of(TickType.TRADE,	time3, CDecimalBD.of("34.15"),
						CDecimalBD.of(100L),
						CDecimalBD.ofUSD2("425.95")),
				new Tick(TickType.TRADE, T("2020-03-05T16:07:45.023Z"), of("10.35"), of(200L), ofRUB2("600.02"), "Test")
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			assertEquals("At #" + i, expected[i], toTest[i].toString());			
		}
	}
	
	@Test
	public void testWithPrice() {
		Tick tick = new Tick(TickType.ASK, time1, of("800.24"), of(400L), ofRUB2("12.48"), "zoom");
		Tick newTick = tick.withPrice(CDecimalBD.of("32.48"));
		assertEquals(TickType.ASK, newTick.getType());
		assertEquals(time1, newTick.getTime());
		assertEquals(CDecimalBD.of("32.48"), newTick.getPrice());
		assertEquals(CDecimalBD.of(400L), newTick.getSize());
		assertEquals(CDecimalBD.ofRUB2("12.48"), newTick.getValue());
		assertEquals("zoom", newTick.getComment());
	}
	
	@Test
	public void testWithTime() {
		Tick tick = new Tick(TickType.TRADE, T("1980-05-01T00:10:00Z"),
				CDecimalBD.of("12.34"),
				CDecimalBD.of(10L),
				CDecimalBD.ofUSD2("1.00"),
				"zulu24"),
			expected = new Tick(TickType.TRADE, T("2006-07-12T13:48:29Z"),
				CDecimalBD.of("12.34"),
				CDecimalBD.of(10L),
				CDecimalBD.ofUSD2("1.00"),
				"zulu24");
		
		assertEquals(expected, tick.withTime(T("2006-07-12T13:48:29Z")));
	}

}
