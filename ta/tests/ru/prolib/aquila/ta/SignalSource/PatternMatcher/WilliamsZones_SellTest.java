package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.indicator.Wz;

public class WilliamsZones_SellTest {
	private TestValue<Double> close;
	private TestValue<Integer> wz;
	private WilliamsZones_Sell matcher;

	@Before
	public void setUp() throws Exception {
		close = new TestValue<Double>();
		wz = new TestValue<Integer>();
		matcher = new WilliamsZones_Sell(wz, close);
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

			{ Wz.RED,   10.0d, false },
			{ Wz.RED,    9.0d, true  },
			{ Wz.RED,    8.0d, true  },
			{ Wz.RED,    8.0d, false },
			{ Wz.RED,    7.0d, true  },
			{ Wz.GRAY,   6.0d, false },
			{ Wz.RED,    5.0d, false },
			{ Wz.RED,    4.0d, true  },
			{ Wz.GRAY,   0.5d, false },
			{ Wz.RED,    1.0d, false },
			{ Wz.GREEN,  2.0d, false },
			{ Wz.GREEN,  3.0d, false },
			{ Wz.RED,    3.0d, false },
			{ Wz.RED,    2.5d, true  },
			{ null,				    2.0d, false },
			{ Wz.RED,    9.0d, false },
			{ Wz.RED,    null, false },
			{ Wz.RED,    2.0d, false },
			{ Wz.RED,    1.1d, true  },

		};
		
		for ( int i = 0; i < fixture.length; i ++ ) {
			wz.addToStackAndUpdate((Integer) fixture[i][0]);
			close.addToStackAndUpdate((Double) fixture[i][1]);
			assertEquals("At sequence #" + i, fixture[i][2], matcher.matches());
		}
	}

}
