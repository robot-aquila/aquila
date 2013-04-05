package ru.prolib.aquila.ta.math;

import org.junit.*;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.math.Sma;
import static org.junit.Assert.*;

public class SmaTest {
	private TestValue<Double> source;
	private Sma sma;

	@Before
	public void setUp() throws Exception {
		source = new TestValue<Double>();
		sma = new Sma(source, 3);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals(3, sma.getPeriods());
		assertSame(source, sma.getSourceValue());
		assertEquals(ValueImpl.DEFAULT_ID, sma.getId());
	}

	@Test
	public void testConstruct3() throws Exception {
		sma = new Sma(source, 3, "zulu");
		assertEquals(3, sma.getPeriods());
		assertSame(source, sma.getSourceValue());
		assertEquals("zulu", sma.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		double fixture[] = {5.0d, 6.0d, 2.0d, 9.0d, 8.0d};
		double expected[] = {5.0d, 5.5d, 4.33333d, 5.66666d, 6.33333d};
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.addToStackAndUpdate(fixture[i]);
			sma.update();
			assertEquals("At sequence #" + i, expected[i], sma.get(), 0.00001d);
		}
		assertEquals(expected.length, sma.getLength());
	}

}
