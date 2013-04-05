package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.SignalSource.IPatternMatcher;

@RunWith(value=Parameterized.class)
public class AC_SellAboveZeroTest {

	@Parameters
	public static Collection<Object[]> dataProvider() {
		return Arrays.asList(new Object[][]{
		// values(a,b,c,d,e), 		 				   expected matched
		
		{new Double[]{                  		   },  false}, // мало
		{new Double[]{ 4d               		   },  false}, // мало
		{new Double[]{ 4d,  5d,  3d     		   },  false}, // мало
		{new Double[]{ 4d,  5d,  3d,  2d  		   },  false}, // мало
		{new Double[]{ 4d,  5d,  3d,  2d,  1d,     },  true},
		{new Double[]{null, 5d,  3d,  2d,  1d,     },  false},
		{new Double[]{ 4d,null,  3d,  2d,  1d,     },  false},
		{new Double[]{ 4d,  5d,null,  2d,  1d,     },  false},
		{new Double[]{ 4d,  5d,  3d,null,  1d,     },  false},
		{new Double[]{ 4d,  5d,  3d,  2d,null,     },  false},
		{new Double[]{-4d,  5d,  3d,  2d,  1d,     },  false}, // a < 0
		{new Double[]{ 4d, -5d,  3d,  2d,  1d,     },  false}, // b < 0
		{new Double[]{ 4d,  5d, -3d,  2d,  1d,     },  false}, // c < 0
		{new Double[]{ 4d,  5d,  3d, -2d,  1d,     },  false}, // d < 0
		{new Double[]{ 4d,  5d,  3d,  2d, -1d,     },  false}, // e < 0
		{new Double[]{-4d, -3d, -4d, -5d, -6d,     },  false}, // all < 0
		{new Double[]{ 4d,3.5d,  3d,  2d,  1d,     },  false}, // b <= a
		{new Double[]{ 4d,  5d,  5d,  2d,  1d,     },  false}, // c >= b
		{new Double[]{ 4d,  5d,  3d,  4d,  1d,     },  false}, // d >= c
		{new Double[]{ 4d,  5d,  3d,  2d,  8d,     },  false}, // e >= d
		
		});
	}
	
	private final Double[] values;
	private final boolean matched;
	
	public AC_SellAboveZeroTest(Double[] values, boolean matched) {
		this.values = values;
		this.matched = matched;
	}
	
	@Test
	public void testMatches() throws Exception {
		TestValue<Double> o = new TestValue<Double>(values);
		for ( int i = 0; i < values.length; i ++ ) {
			o.update();
		}
		IPatternMatcher matcher = new AC_SellAboveZero(o);
		assertEquals(matched, matcher.matches());	
	}

}
