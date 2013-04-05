package ru.prolib.aquila.ta.math;

import org.junit.*;
import ru.prolib.aquila.ta.*;
import static org.junit.Assert.*;

public class QuikSmmaTest {
	private TestValue<Double> source;
	private QuikSmma ma;
	
	@Before
	public void setUp() throws Exception {
		source = new TestValue<Double>();
		ma = new QuikSmma(source, 3);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals(3, ma.getPeriods());
		assertSame(source, ma.getSourceValue());
		assertEquals(ValueImpl.DEFAULT_ID, ma.getId());
	}

	@Test
	public void testConstruct3() throws Exception {
		ma = new QuikSmma(source, 10, "zulu");
		assertEquals(10, ma.getPeriods());
		assertSame(source, ma.getSourceValue());
		assertEquals("zulu", ma.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		double fixture[] = {5.0d, 1.5d, 8.5d, 7.0d, 3.5d, 1.2d};
		double expected[] = {5.0d, 3.25d, 5.0d, 6.33333d, 5.38889d, 2.5037d};
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.addToStackAndUpdate(fixture[i]);
			ma.update();
			assertEquals("At sequence #" + i, expected[i], ma.get(), 0.00001d);
		}
		assertEquals(ma.getLength(), expected.length);
	}

}
