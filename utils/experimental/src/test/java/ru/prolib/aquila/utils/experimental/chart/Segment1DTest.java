package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class Segment1DTest {
	private Segment1D segment;

	@Before
	public void setUp() throws Exception {
		segment = new Segment1D(5, 20);
	}
	
	@Test
	public void testCtor() {
		assertEquals(5, segment.getStart());
		assertEquals(20, segment.getLength());
		assertEquals(24, segment.getEnd());
		assertEquals(15, segment.getMidpoint());
	}
	
	@Test
	public void testGetMidpoint() {
		segment = new Segment1D(10, 29);
		assertEquals(24, segment.getMidpoint());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(segment.equals(segment));
		assertFalse(segment.equals(null));
		assertFalse(segment.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vStart = new Variant<>(5, 2),
				vLength = new Variant<>(vStart, 20, 100);
		Variant<?> iterator = vLength;
		int foundCnt = 0;
		Segment1D x, found = null;
		do {
			x = new Segment1D(vStart.get(), vLength.get());
			if ( segment.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(5, found.getStart());
		assertEquals(20, found.getLength());
	}
	
	@Test
	public void testToString() {
		String expected = "Segment1D[start=5, length=20]";
		assertEquals(expected, segment.toString());
	}
	
	@Test
	public void testContains_Seg() {
		assertTrue(segment.contains(segment));
		assertTrue(segment.contains(new Segment1D(5, 1)));
		assertTrue(segment.contains(new Segment1D(19, 1)));
		assertTrue(segment.contains(new Segment1D(10, 5)));
		assertFalse(segment.contains(new Segment1D(0, 40)));
		assertFalse(segment.contains(new Segment1D(5, 25)));
		assertFalse(segment.contains(new Segment1D(1, 20)));
	}
	
	@Test
	public void testContains_Int() {
		assertFalse(segment.contains(-200));
		assertFalse(segment.contains(-10));
		assertFalse(segment.contains(-1));
		assertFalse(segment.contains(0));
		assertFalse(segment.contains(1));
		assertFalse(segment.contains(2));
		assertFalse(segment.contains(3));
		assertFalse(segment.contains(4));
		assertTrue(segment.contains(5));
		assertTrue(segment.contains(6));
		assertTrue(segment.contains(7));
		// ...
		assertTrue(segment.contains(22));
		assertTrue(segment.contains(23));
		assertTrue(segment.contains(24));
		assertFalse(segment.contains(25));
		assertFalse(segment.contains(26));
		assertFalse(segment.contains(27));
	}

}
