package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.indicator.Wz;

public class WilliamsZones_BuyTest {
	private TestValue<Double> close;
	private TestValue<Integer> wz;
	private WilliamsZones_Buy matcher;

	@Before
	public void setUp() throws Exception {
		close = new TestValue<Double>();
		wz = new TestValue<Integer>();
		matcher = new WilliamsZones_Buy(wz, close);
	}
	
	@Test
	public void testAccessors() {
		assertSame(close, matcher.getSourceValue());
		assertSame(wz, matcher.getWilliamsZones());
	}
	
	@Test
	public void testMatches() throws Exception {
		Object fixture[][] = {
			// zone, close, matched?
			{ Wz.GREEN, 10.0d, false },
			{ Wz.GREEN, 11.0d, true  },
			{ Wz.GREEN, 12.0d, true  },
			{ Wz.GREEN, 12.0d, false },
			{ Wz.GREEN, 13.0d, true  },
			{ Wz.GRAY,  14.0d, false },
			{ Wz.GREEN, 15.0d, false },
			{ Wz.GREEN, 16.0d, true  },
			{ Wz.GRAY,   0.5d, false },
			{ Wz.GREEN,  1.0d, false },
			{ Wz.RED,    2.0d, false },
			{ Wz.RED,    3.0d, false },
			{ Wz.GREEN,  3.0d, false },
			{ Wz.GREEN,  3.5d, true  },
			{ null,				    8.0d, false },
			{ Wz.GREEN,  9.0d, false },
			{ Wz.GREEN,  null, false },
			{ Wz.GREEN,  2.0d, false },
			{ Wz.GREEN,  2.1d, true },
			
		};
		
		for ( int i = 0; i < fixture.length; i ++ ) {
			wz.addToStackAndUpdate((Integer) fixture[i][0]);
			close.addToStackAndUpdate((Double) fixture[i][1]);
			assertEquals("At sequence #" + i, fixture[i][2], matcher.matches());
		}
	}

}
