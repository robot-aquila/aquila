package ru.prolib.aquila.ta.indicator;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.ValueImpl;

/**
 * 2012-02-07
 * $Id: StdevTest.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class StdevTest {
	private ValueImpl<Double> source;
	private Stdev value;

	@Before
	public void setUp() throws Exception {
		source = new ValueImpl<Double>();
		value = new Stdev(source, 30);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals(30, value.getPeriod());
		assertSame(source, value.getSource());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new Stdev(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new Stdev(source, 1);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double fix[][] = {
			// source, expected
			{ 570.87, null },
			{ 543.63, 19.2615887195216 },
			{ 513.84, 28.5244999956178 },
			{ 533.68, 23.7304424737509 },
			{ 532.12, 20.8904779744265 },
			{ 543.24, 18.7716261060854 },
			{ 522.76, 18.2751458856792 },
			{ 518.86, 18.1147374887331 },
			{ 482.96, 24.2195948562316 },
			{ 498.87, 24.7555619115292 },
			{ 479.16, 27.4174174899494 },
			{ 456.99, 32.1495207089963 },
			{ 429.61, 39.0779059814806 },
			{ 435.03, 42.5238142976095 },
			{ 409.18, 47.7868914582032 },
			{ 351.54, 58.9320173165657 },
			{ 355.34, 65.613813618018  },
			{ 347.10, 71.0534271962998 },
			{ 397.34, 71.2333275907361 },
			{ 398.56, 71.1291352992643 },
			{ 386.67, 71.4585309546066 },
			{ 392.99, 71.2827926571647 },
			{ 395.89, 70.880348344396  },
			{ 384.65, 70.8508184727662 },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			source.add(fix[i][0]);
			String msg = "At #" + i;
			if ( fix[i][1] == null ) {
				assertNull(msg, value.calculate());
			} else {
				assertEquals(msg, fix[i][1], value.calculate(), 0.0000001d);
			}
		}
	}

}
