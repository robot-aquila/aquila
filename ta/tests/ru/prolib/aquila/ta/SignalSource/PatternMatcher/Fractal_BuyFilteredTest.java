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
public class Fractal_BuyFilteredTest {

	@Parameters
	public static Collection<Object[]> dataProvider() {
		return Arrays.asList(new Object[][]{
				
	// values, 		 			  filter value, expected matched
	{new Double[]{                }, 1d,		false}, // недостаточно значений
	{new Double[]{ 1d,1d,1d,1d,1d }, 2d,		false}, // нету пика
	{new Double[]{ 1d,0d,1d,0d,0d }, 0d,		false}, // пик не высший
	{new Double[]{ 0d,1d,1d,0d,0d }, 0d,		false}, // пик не высший
	{new Double[]{ 0d,0d,1d,1d,0d }, 0d,		false}, // пик не высший
	{new Double[]{ 0d,0d,1d,0d,1d }, 0d,		false}, // пик не высший
	{new Double[]{-1d,2d,4d,1d,3d }, 6d,		false}, // ниже фильтра
	{new Double[]{-1d,2d,4d,1d,3d }, 1d,		true},
	{new Double[]{-1d,2d,4d,1d,3d }, null,		false}, // нет фильтра
	
		});
	}
	
	private final Double[] values;
	private final Double filter;
	private final boolean matched;
	
	public Fractal_BuyFilteredTest(Double[] values, Double filter,
			boolean matched)
	{
		this.values = values;
		this.filter = filter;
		this.matched = matched;
	}
	
	@Test
	public void testMatches() throws Exception {
		TestValue<Double> hi = new TestValue<Double>();
		TestValue<Double> f = new TestValue<Double>();
		for ( int i = 0; i < values.length; i ++ ) {
			hi.addToStackAndUpdate(values[i]);
			f.addToStackAndUpdate(filter);
		}
		IPatternMatcher matcher = new Fractal_BuyFiltered(hi, f);
		assertEquals(matched, matcher.matches());
	}

}
