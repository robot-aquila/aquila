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

}
