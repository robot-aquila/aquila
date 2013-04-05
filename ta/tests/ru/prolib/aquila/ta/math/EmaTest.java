package ru.prolib.aquila.ta.math;

import org.junit.*;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.math.Ema;
import static org.junit.Assert.*;

public class EmaTest {
	private TestValue<Double> source;
	private Ema ema;

	@Before
	public void setUp() throws Exception {
		source = new TestValue<Double>();
		ema = new Ema(source, 5);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(source, ema.getSourceValue());
		assertEquals(5, ema.getPeriods());
		assertEquals(ValueImpl.DEFAULT_ID, ema.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		ema = new Ema(source, 5, "foobar");
		assertSame(source, ema.getSourceValue());
		assertEquals(5, ema.getPeriods());
		assertEquals("foobar", ema.getId());
	}
	
	@Test
	public void testUpdate() throws Exception {
		double fixture[] = {5.0d, 6.0d, 2.0d};
		double expected[] = {5.0d, 5.333333d, 4.22222d};
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.addToStackAndUpdate(fixture[i]);
			ema.update();
			assertEquals("At sequence #" + i, expected[i], ema.get(), 0.00001d);
		}
		assertEquals(expected.length, ema.getLength());
	}

}
