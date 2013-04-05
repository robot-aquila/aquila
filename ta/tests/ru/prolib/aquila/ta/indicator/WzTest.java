package ru.prolib.aquila.ta.indicator;


import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.*;

public class WzTest {
	private ValueImpl<Double> ao,ac;
	private Wz wz;

	@Before
	public void setUp() throws Exception {
		ao = new ValueImpl<Double>();
		ac = new ValueImpl<Double>();
		wz = new Wz(ao, ac);
	}
	
	@Test
	public void testConstruct_Ok() {
		assertSame(ao, wz.getFirstSource());
		assertSame(ac, wz.getSecondSource());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfFirstSourceIsNull() throws Exception {
		new Wz(null, ac);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSecondSourceIsNull() throws Exception {
		new Wz(ao, null);
	}

	@Test
	public void testCalculate() throws Exception {
		Object[][] fixture = {
		   // ao, ac, expected
			{ 1d, 1d, Wz.GRAY  },
			{ 2d, 2d, Wz.GREEN },
			{ 3d, 3d, Wz.GREEN },
			{ 4d, 2d, Wz.GRAY  },
			{ 3d, 4d, Wz.GRAY  },
			{ 2d, 3d, Wz.RED   },
			{ 1d, 2d, Wz.RED   },
			{ 2d, 3d, Wz.GREEN },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			ao.add((Double) fixture[i][0]);
			ac.add((Double) fixture[i][1]);
			String msg = "At sequence #" + i;
			if ( fixture[i][2] == null ) {
				assertNull(msg, wz.calculate());
			} else {
				assertEquals(msg, (Integer)fixture[i][2], wz.calculate());
			}
		}
	}

}
