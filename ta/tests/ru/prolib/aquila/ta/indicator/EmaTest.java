package ru.prolib.aquila.ta.indicator;

import org.junit.*;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.Ema;
import static org.junit.Assert.*;

public class EmaTest {
	private ValueImpl<Double> source;
	private Ema ema;

	@Before
	public void setUp() throws Exception {
		source = new ValueImpl<Double>(); source.add(null);
		ema = new Ema(source, 5);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(source, ema.getSource());
		assertEquals(5, ema.getPeriod());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		ema = new Ema(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		ema = new Ema(source, 1);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double fixture[] =  { null, 5.0d, 6.0d,      2.0d,     null, 1d };
		Double expected[] = { null, 5.0d, 5.333333d, 4.22222d, null, 1d };
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.set(fixture[i]);
			String msg = "At sequence #" + i;
			if ( expected[i] == null ) {
				assertNull(ema.calculate());
			} else {
				assertEquals(msg, expected[i], ema.calculate(), 0.00001d);
			}
		}
	}

}
