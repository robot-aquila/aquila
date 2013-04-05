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
public class AC_SellBelowZeroTest {

	@Parameters
	public static Collection<Object[]> dataProvider() {
		return Arrays.asList(new Object[][]{
		// values(a,b,c,d), 		 		expected matched
		{new Double[]{                  }, 	false}, // мало
		{new Double[]{-1d               }, 	false}, // мало
		{new Double[]{-1d, -1d, -1d     },  false}, // мало
		{new Double[]{ 2d,  3d,  2d,  1d},  false}, // all > 0
		{new Double[]{ 1d, -1d, -1d, -1d},	false}, // a > 0
		{new Double[]{-1d,  1d, -1d, -1d},	false}, // b > 0
		{new Double[]{-1d, -1d,  1d, -1d},	false}, // c > 0
		{new Double[]{-1d, -1d, -1d,  1d},	false}, // d > 0
		{new Double[]{-1d, -1d, -2d, -3d},	false}, // b <= a
		{new Double[]{-3d, -1d, -1d, -3d},	false}, // c >= b
		{new Double[]{-3d, -1d, -2d, -2d},	false}, // d >= c
		{new Double[]{null,-1d, -2d, -3d},	false},
		{new Double[]{-3d,null, -2d, -3d},	false},
		{new Double[]{-3d, -1d,null, -3d},	false},
		{new Double[]{-3d, -1d, -2d,null},	false},
		{new Double[]{-3d, -1d, -2d, -3d},	true},
		
		});
	}
	
	private final Double[] values;
	private final boolean matched;
	
	public AC_SellBelowZeroTest(Double[] values, boolean matched) {
		this.values = values;
		this.matched = matched;
	}
	
	@Test
	public void testMatches() throws Exception {
		TestValue<Double> o = new TestValue<Double>(values);
		for ( int i = 0; i < values.length; i ++ ) {
			o.update();
		}
		IPatternMatcher matcher = new AC_SellBelowZero(o);
		assertEquals(matched, matcher.matches());	
	}

}
