package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-12<br>
 * $Id: TRTest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TRTest {
	private static final Double fixture[][] = {
		// hi, lo, close, tr expected
		{ 48.70d, 47.79d, 48.16d, 0.91d },
		{ 49.35d, 48.86d, 49.32d, 1.19d },// HL=0.49 HCp=1.19 LCp=0.70: TR=1.19
		{ 49.92d, 49.50d, 49.91d, 0.60d },
		{ 50.19d, 49.87d, 50.13d, 0.32d },// HL=0.32 HCp=0.28 LCp=0.04: TR=0.32
		{ 50.12d, 49.20d, 49.53d, 0.93d },
	};
	private EditableCandleSeries source;
	private TR tr;

	@Before
	public void setUp() throws Exception {
		source = new CandleSeriesImpl(Timeframe.M1);
		tr = new TR("foo", source);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals("foo", tr.getId());
		assertSame(source, tr.getSource());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		tr = new TR(source);
		assertEquals(Series.DEFAULT_ID, tr.getId());
		assertSame(source, tr.getSource());
	}
	
	@Test
	public void testCalculate() throws Exception {
		DateTime time = new DateTime(2013, 10, 11, 11, 12, 34);
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(new Candle(source.getTimeframe()
				.getInterval(time.plusMinutes(i)), 0d, fixture[i][0],
					fixture[i][1], fixture[i][2], 0));
			String msg = "At #" + i;
			assertEquals(msg, fixture[i][3], tr.get(), 0.01d);
			assertEquals(msg, fixture[i][3], tr.get(i),0.01d);
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(tr.equals(tr));
		assertFalse(tr.equals(null));
		assertFalse(tr.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("foo")
			.add("bar");
		Variant<CandleSeries> vSrc = new Variant<CandleSeries>(vId)
			.add(new CandleSeriesImpl(Timeframe.M1))
			.add(new CandleSeriesImpl(Timeframe.M10, "zulu46"));
		Variant<?> iterator = vSrc;
		int foundCnt = 0;
		TR x, found = null;
		do {
			x = new TR(vId.get(), vSrc.get());
			if ( tr.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertEquals(source, found.getSource());
	}

}
