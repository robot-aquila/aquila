package ru.prolib.aquila.ta.indicator;


import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.*;

public class ShiftRightTest {
	private TestValue<Integer> source;
	private ShiftRight<Integer> v;

	@Before
	public void setUp() throws Exception {
		source = new TestValue<Integer>();
		v = new ShiftRight<Integer>(source, 3);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals(3, v.getPeriod());
		assertSame(source, v.getSource());
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new ShiftRight<Integer>(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new ShiftRight<Integer>(source, 1);
	}

	@Test
	public void testCalculate() throws Exception {
		Integer fixture[] = { 1, 2, 3, null, 4, 5, 6, 7 };
		Integer expected[] = { null, null, null, 1, 2, 3, null, 4 };
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(fixture[i]);
			String msg = "At sequence #" + i;
			if ( expected[i] == null ) {
				assertNull(msg, v.calculate());
			} else {
				assertEquals(msg, expected[i], v.calculate());
			}
		}
	}

}
