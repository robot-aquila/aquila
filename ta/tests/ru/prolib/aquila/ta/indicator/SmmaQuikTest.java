package ru.prolib.aquila.ta.indicator;


import org.junit.*;

import ru.prolib.aquila.ta.*;
import static org.junit.Assert.*;

public class SmmaQuikTest {
	private ValueImpl<Double> source;
	private SmmaQuik ma;
	
	@Before
	public void setUp() throws Exception {
		source = new ValueImpl<Double>();
		ma = new SmmaQuik(source, 3);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals(3, ma.getPeriod());
		assertSame(source, ma.getSource());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new SmmaQuik(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new SmmaQuik(source, 1);
	}

	@Test
	public void testCalculate() throws Exception {
		Double fixture[][] = {
				//{ null, null     },
				{ 5.0d, 5.0d     },
				{ 1.5d, 3.25d    },
				{ 8.5d, 5.0d     },
				{ 7.0d, 6.33333d },
				{ 3.5d, 5.38889d },
				{ 1.2d, 2.5037d  },
				//{ null, null 	 },
				//{ 5.0d, null	 },
				//{ 1.5d, null	 },
				//{ 8.5d, 5.0d	 },
		};
		assertNull(ma.calculate()); // zero length
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(fixture[i][0]);
			String msg = "At sequence #" + i;
			if ( fixture[i][1] == null ) {
				assertNull(msg, ma.calculate());
			} else {
				assertEquals(msg, fixture[i][1], ma.calculate(), 0.00001d);
			}
		}
	}

}
