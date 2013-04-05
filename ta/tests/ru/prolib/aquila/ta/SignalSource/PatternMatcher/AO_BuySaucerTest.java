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
public class AO_BuySaucerTest {

	@Parameters
	public static Collection<Object[]> dataProvider() {
		return Arrays.asList(new Object[][]{
			// values, 		 expected matched
			{new Double[]{             }, false},
			{new Double[]{ 1d          }, false},
			{new Double[]{-1d,  1d,  1d}, false},
			{new Double[]{ 1d, -1d,  1d}, false},
			{new Double[]{ 1d,  1d, -1d}, false},
			{new Double[]{ 1d,  2d,  1d}, false},
			{new Double[]{ 2d,  1d,  1d}, false},
			{new Double[]{ 2d,  1d,  2d},  true},
		});
	}
	
	private final Double[] values;
	private final boolean matched;
	
	public AO_BuySaucerTest(Double[] values, boolean matched) {
		this.values = values;
		this.matched = matched;
	}
	
	@Test
	public void testMatches() throws Exception {
		TestValue<Double> o = new TestValue<Double>(values);
		for ( int i = 0; i < values.length; i ++ ) {
			o.update();
		}
		IPatternMatcher matcher = new AO_BuySaucer(o);
		assertEquals(matched, matcher.matches());	
	}

}
