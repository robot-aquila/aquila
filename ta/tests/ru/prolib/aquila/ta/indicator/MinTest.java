package ru.prolib.aquila.ta.indicator;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ValueImpl;

public class MinTest {
	private ValueImpl<Double> src;
	private Min min;

	@Before
	public void setUp() throws Exception {
		src = new TestValue<Double>();
		min = new Min(src, 5);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(src, min.getSource());
		assertEquals(5, min.getPeriod());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new Min(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new Min(src, 1);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double fix[][] = {
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
			src.add(fix[i][0]);
			String msg = "At #" + i;
			if ( fix[i][1] == null ) {
				assertNull(msg, min.calculate());
			} else {
				assertEquals(msg, fix[i][1], min.calculate(), 0.1d);
			}
		}
	}

}
