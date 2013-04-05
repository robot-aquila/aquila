package ru.prolib.aquila.ta.math;


import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ValueImpl;

/**
 * 2012-02-07
 * $Id: StdevTest.java 199 2012-02-07 21:10:33Z whirlwind $
 */
public class StdevTest {
	TestValue<Double> source;
	Stdev value;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	@Before
	public void setUp() throws Exception {
		source = new TestValue<Double>();
		value = new Stdev(source, 30, "foobar");
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertEquals("foobar", value.getId());
		assertEquals(30, value.getPeriods());
		assertSame(source, value.getSourceValue());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		value = new Stdev(source, 5);
		assertEquals(ValueImpl.DEFAULT_ID, value.getId());
		assertEquals(5, value.getPeriods());
		assertSame(source, value.getSourceValue());
	}
	
	@Test
	public void testUpdate() throws Exception {
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
			source.addToStackAndUpdate(fix[i][0]);
			value.update();
			if ( fix[i][1] == null ) {
				assertNull(value.get());
			} else {
				assertEquals("At #" + i, fix[i][1], value.get(), 0.0000001d);
			}
		}
	}

}
