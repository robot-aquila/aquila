package ru.prolib.aquila.ta;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.ta.BarPattern;
import ru.prolib.aquila.util.Variant;

public class BarPatternTest {
	BarPattern pattern;

	@Before
	public void setUp() throws Exception {
		pattern = new BarPattern(2, 0, 1, 2);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertEquals(3, pattern.getHeight());
		assertEquals(2, pattern.getTop());
		assertEquals(0, pattern.getBottom());
		assertEquals(1, pattern.getOpen());
		assertEquals(2, pattern.getClose());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> t = new Variant<Integer>(new Integer[]{ 1, 5, 2 });
		Variant<Integer> b = new Variant<Integer>(new Integer[]{ 0,-3 }, t);
		Variant<Integer> o = new Variant<Integer>(new Integer[]{ 1, 3 }, b);
		Variant<Integer> c = new Variant<Integer>(new Integer[]{ 0, 2 }, o);
		BarPattern expected = pattern;
		BarPattern found = null, current = null;
		int numfound = 0;
		Variant<?> root = c;
		do {
			current = new BarPattern(t.get(), b.get(), o.get(), c.get());
			if ( current.equals(expected) ) {
				numfound ++;
				found = current;
			}
		} while ( root.next() );
		assertEquals(1, numfound);
		assertNotNull(found);
		assertEquals(2, found.getTop());
		assertEquals(0, found.getBottom());
		assertEquals(1, found.getOpen());
		assertEquals(2, found.getClose());
		assertEquals(expected, found);
		assertNotSame(expected, found);
		
		assertEquals(expected, expected);
	}

}
