package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

public class ATRTest {
	private EventSystem es;
	private EditableCandleSeries candles;
	private ATR atr;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		candles = new CandleSeriesImpl(es, Timeframe.M1);
		atr = new ATR(es, "bar", candles, 5, 1024);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testConstruct4() throws Exception {
		assertEquals("bar", atr.getId());
		assertEquals(new TR(es, candles), atr.getSource());
		assertEquals(5, atr.getPeriod());
		assertEquals(1024, atr.getStorageLimit());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		atr = new ATR(es, "foo", candles, 10);
		assertEquals("foo", atr.getId());
		assertEquals(new TR(es, candles), atr.getSource());
		assertEquals(10, atr.getPeriod());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, atr.getStorageLimit());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		atr = new ATR(es, candles, 15);
		assertEquals("ATR(15)", atr.getId());
		assertEquals(new TR(es, candles), atr.getSource());
		assertEquals(15, atr.getPeriod());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, atr.getStorageLimit());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(atr.equals(atr));
		assertFalse(atr.equals(null));
		assertFalse(atr.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("bar")
			.add("foo");
		Variant<CandleSeries> vSrc = new Variant<CandleSeries>(vId)
			.add(candles)
			.add(new CandleSeriesImpl(es, Timeframe.M1, "zulubaba"));
		Variant<Integer> vPer = new Variant<Integer>(vSrc)
			.add(5)
			.add(24);
		Variant<Integer> vLen = new Variant<Integer>(vPer)
			.add(1024)
			.add(128);
		Variant<?> iterator = vLen;
		int foundCnt = 0;
		ATR x, found = null;
		do {
			x = new ATR(es, vId.get(), vSrc.get(), vPer.get(), vLen.get());
			if ( atr.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("bar", found.getId());
		assertEquals(new TR(es, candles), found.getSource());
		assertEquals(5, found.getPeriod());
		assertEquals(1024, found.getStorageLimit());
	}

}
