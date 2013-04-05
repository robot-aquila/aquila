package ru.prolib.aquila.ta.math;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.Wz;

public class WilliamsZonesTest {
	private TestValue<Double> ao,ac;
	private WilliamsZones wz;

	@Before
	public void setUp() throws Exception {
		ao = new TestValue<Double>();
		ac = new TestValue<Double>();
		wz = new WilliamsZones(ao, ac);
	}
	
	@Test
	public void testConstruct2() {
		assertSame(ao, wz.getOscillator1());
		assertSame(ac, wz.getOscillator2());
		assertEquals(ValueImpl.DEFAULT_ID, wz.getId());
	}

	@Test
	public void testConstruct3() {
		wz = new WilliamsZones(ao, ac, "williams");
		assertSame(ao, wz.getOscillator1());
		assertSame(ac, wz.getOscillator2());
		assertEquals("williams", wz.getId());
	}

	@Test
	public void testUpdate() throws Exception {
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
			ao.addToStackAndUpdate((Double) fixture[i][0]);
			ac.addToStackAndUpdate((Double) fixture[i][1]);
			wz.update();
			assertEquals("At sequence #" + i, (Integer)fixture[i][2], wz.get()); 
		}
	}

}
