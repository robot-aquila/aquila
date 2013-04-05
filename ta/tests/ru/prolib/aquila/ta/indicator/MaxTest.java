package ru.prolib.aquila.ta.indicator;


import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.ValueImpl;

public class MaxTest {
	private ValueImpl<Double> src;
	private Max max;

	@Before
	public void setUp() throws Exception {
		src = new ValueImpl<Double>();
		max = new Max(src, 5);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(src, max.getSource());
		assertEquals(5, max.getPeriod());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new Max(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new Max(src, 1);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double fix[][] = {
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
			src.add(fix[i][0]);
			String msg = "At #" + i;
			if ( fix[i][1] == null ) {
				assertNull(msg, max.calculate());
			} else {
				assertEquals(msg, fix[i][1], max.calculate(), 0.1d);
			}
		}
	}

}
