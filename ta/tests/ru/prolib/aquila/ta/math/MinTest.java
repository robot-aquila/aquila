package ru.prolib.aquila.ta.math;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ValueImpl;

public class MinTest {
	TestValue<Double> src;
	Min min;

	@Before
	public void setUp() throws Exception {
		src = new TestValue<Double>();
		min = new Min(src, 5, "foobar");
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertSame(src, min.getSourceValue());
		assertEquals(5, min.getPeriods());
		assertEquals("foobar", min.getId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		min = new Min(src, 10);
		assertSame(src, min.getSourceValue());
		assertEquals(10, min.getPeriods());
		assertEquals(ValueImpl.DEFAULT_ID, min.getId());
	}
	
	@Test
	public void testUpdate_SkipsIfZeroLength() throws Exception {
		min.update();
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Object fix[][] = {
				// src val, expected min
				{ null, null },
				{ 140d, 140d },
				{ 130d, 130d },
				{ null, 130d },
				{ 230d, 130d },
				{ 200d, 130d },
				{ 160d, 130d },
				{ 180d, 160d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			src.addToStackAndUpdate((Double)fix[i][0]);
			min.update();
			String msg = "At #" + i;
			if ( fix[i][1] == null ) {
				assertNull(msg, min.get());
			} else {
				assertEquals(msg, (Double)fix[i][1], min.get(), 0.1d);
			}
		}

	}

}
