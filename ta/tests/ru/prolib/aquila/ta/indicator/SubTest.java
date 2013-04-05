package ru.prolib.aquila.ta.indicator;


import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.*;

/**
 * 2012-04-06
 * $Id: SubTest.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class SubTest {
	private ValueImpl<Double> src1,src2;
	private Sub sub;
	
	@Before
	public void setUp() throws Exception {
		src1 = new ValueImpl<Double>();
		src2 = new ValueImpl<Double>();
		sub = new Sub(src1, src2);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(src1, sub.getFirstSource());
		assertSame(src2, sub.getSecondSource());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfFirstSourceIsNull() throws Exception {
		new Sub(null, src2);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSecondSourceIsNull() throws Exception {
		new Sub(src1, null);
	}

	@Test
	public void testCalculate() throws Exception {
		Double fixture[][] = {
			// src1, src2, expected
			{ null,  1.0d,  null },
			{ 1.0d,  null,  null },
			{ null,  null,  null },
			{ 1.0d,  2.0d,  -1.0d},
			{ 2.0d,  1.0d,   1.0d},
			{ 3.0d,  3.0d,   0.0d},
			{-5.0d, -1.0d,  -4.0d},
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			src1.add(fixture[i][0]);
			src2.add(fixture[i][1]);
			String msg = "At sequence #" + i;
			if ( fixture[i][2] == null ) {
				assertNull(msg, sub.calculate());
			} else {
				assertEquals(msg, fixture[i][2], sub.calculate(), 0.1d);
			}
		}
	}

}
