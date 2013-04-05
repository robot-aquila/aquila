package ru.prolib.aquila.stat;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.util.Variant;

/**
 * 2012-02-02
 * $Id: PositionChangeTest.java 196 2012-02-02 20:24:38Z whirlwind $
 */
public class PositionChangeTest {
	
	@Test
	public void testConstruct3() throws Exception {
		PositionChange pc = new PositionChange(125, -5, 145000d);
		assertEquals(125, pc.getBar());
		assertEquals(-5, pc.getQty());
		assertEquals(145000d, pc.getPrice(), 0.001d);
		assertNull(pc.getComment());
	}
	
	@Test
	public void testConstruct4() throws Exception {
		PositionChange pc = new PositionChange(80, 20, 8000d, "foobar");
		assertEquals(80, pc.getBar());
		assertEquals(20, pc.getQty());
		assertEquals(8000d, pc.getPrice(), 0.001d);
		assertEquals("foobar", pc.getComment());
	}
	
	@Test
	public void testEquals_Complex() throws Exception {
		Variant<Integer> bar = new Variant<Integer>(new Integer[]{5, 3, 4});
		Variant<Integer> qty = new Variant<Integer>(new Integer[]{-2, 1,}, bar);
		Variant<Double> price = new Variant<Double>(new Double[]{ 10d, 20d}, qty);
		Variant<String> comment = new Variant<String>(new String[] { null, "foo"}, price);
		Variant<?> root = comment;
		
		PositionChange expected = new PositionChange(3, -2, 20d, "foo");
		PositionChange found = null;
		int numfound = 0;
		do {
			PositionChange current = new PositionChange(bar.get(), qty.get(),
					price.get(), comment.get());
			if ( current.equals(expected) ) {
				found = current;
				numfound ++;
			}
		} while ( root.next() );
		assertEquals(1, numfound);
		assertEquals(3, found.getBar());
		assertEquals(-2, found.getQty());
		assertEquals(20d, found.getPrice(), 0.01d);
		assertEquals("foo", found.getComment());
	}
	
	@Test
	public void testEquals_Null() throws Exception {
		PositionChange change = new PositionChange(1, 2, 3d, "foobar");
		assertFalse(change.equals(null));
	}
	
	@Test
	public void testEquals_SameInstance() throws Exception {
		PositionChange change = new PositionChange(1, 2, 3d, "foobar");
		assertTrue(change.equals(change));
	}
	
	@Test
	public void testEquals_AnotherClass() throws Exception {
		PositionChange change = new PositionChange(1, 2, 3d, "foobar");
		assertFalse(change.equals(this));
	}

}
