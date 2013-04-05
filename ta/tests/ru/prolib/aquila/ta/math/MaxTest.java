package ru.prolib.aquila.ta.math;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ValueImpl;

public class MaxTest {
	TestValue<Double> src;
	Max max;

	@Before
	public void setUp() throws Exception {
		src = new TestValue<Double>();
		max = new Max(src, 5, "foobar");
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertSame(src, max.getSourceValue());
		assertEquals(5, max.getPeriods());
		assertEquals("foobar", max.getId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		max = new Max(src, 10);
		assertSame(src, max.getSourceValue());
		assertEquals(10, max.getPeriods());
		assertEquals(ValueImpl.DEFAULT_ID, max.getId());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Object fix[][] = {
			// src val, expected max
			{null, null},
			{null, null},
			{ 20d, 20d },
			{ 19d, 20d },
			{null, 20d },
			{ 23d, 23d },
			{ 20d, 23d },
			{ 16d, 23d },
			{ 18d, 23d },
			{ 15d, 23d },
			{ 14d, 20d },
			{ 15d, 18d },
			{ 16d, 18d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			src.addToStackAndUpdate((Double)fix[i][0]);
			max.update();
			String msg = "At #" + i;
			if ( fix[i][1] == null ) {
				assertNull(msg, max.get());
			} else {
				assertEquals(msg, (Double)fix[i][1], max.get(), 0.1d);
			}
		}
	}
	
	@Test
	public void testUpdate_SkipsIfZeroLength() throws Exception {
		max.update();
	}

}
