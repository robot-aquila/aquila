package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class YearPointTest {
	private YearPoint point;

	@Before
	public void setUp() throws Exception {
		point = new YearPoint(2015);
	}
	
	@Test
	public void testCtor1() {
		assertEquals(2015, point.getYear());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(point.equals(point));
		assertFalse(point.equals(null));
		assertFalse(point.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(point.equals(new YearPoint(2015)));
		assertFalse(point.equals(new YearPoint(2011)));
	}
	
	@Test
	public void testToString() {
		assertEquals("YearPoint[2015]", point.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(45, 1276819)
				.append(2015)
				.toHashCode(), point.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(point.compareTo(new YearPoint(2015)) == 0);
		assertTrue(point.compareTo(new YearPoint(2016)) < 0);
		assertTrue(point.compareTo(new YearPoint(2014)) > 0);
	}

}
